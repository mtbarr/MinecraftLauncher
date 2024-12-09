package launcher.core

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json
import launcher.core.config.VersionConfig
import launcher.core.extensions.withSeparator
import launcher.core.file.GameFolders
import launcher.core.file.ResourceFile
import launcher.core.file.ResourceFileType.NATIVE
import launcher.core.file.ResourceFileType.SERVER_DATA
import launcher.core.file.download.DownloadProgress
import launcher.core.file.download.FileDownloaderAdapter
import launcher.core.version.Version
import launcher.core.version.forge.MappedForgeVersion
import launcher.core.version.forge.version.ForgeVersion
import launcher.core.version.minecraft.MappedMinecraftVersion
import launcher.core.version.minecraft.assets.AssetIndexFile
import launcher.core.version.minecraft.version.MojangVersion

private const val MOJANG_REPOSITORY_URL = "https://resources.download.minecraft.net"
private const val DEFAULT_VERSIONS_FILE_URL = "https://fly.storage.tigris.dev/minecraft-launcher-data/versions.json"
private const val DOWNLOAD_PARALLELISM = 15

private val coroutineDispatcher = Dispatchers.IO.limitedParallelism(DOWNLOAD_PARALLELISM)
private val coroutineScope = CoroutineScope(coroutineDispatcher + SupervisorJob())
private val semaphore = Semaphore(DOWNLOAD_PARALLELISM)

class Launcher private constructor(
    val platformData: PlatformData,
    val gameFolders: GameFolders,
    val json: Json,
    val versionsFileUrl: String,
) {
    private val fileDownloader = FileDownloaderAdapter(createHttpClient())

    var selectedVersion: Version? = null
    var selectedMinecraftVersion: MappedMinecraftVersion? = null
    var selectedForgeVersion: MappedForgeVersion? = null

    val downloadFlow = MutableSharedFlow<DownloadProgress>()

    suspend fun downloadVersionsFile() {
        val file =
            downloadIfNotExists(
                location = gameFolders.baseDir withSeparator "versions.json",
                remoteUrl = versionsFileUrl,
            ).await()

        val versionConfig = json.decodeFromString<VersionConfig>(file.decodeToString())
        selectedVersion = versionConfig.versions.first().toVersion(gameFolders)
    }

    suspend fun downloadResources() {
        val selectedVersion = requireNotNull(selectedVersion)

        loadMinecraftVersion(selectedVersion)
        selectedVersion.forgeVersionInfoResource?.let { loadForgeVersion(selectedVersion, it) }

        selectedVersion.resources.filter { it.type != SERVER_DATA }
            .map { resource -> downloadIfNotExists(resource) }
            .awaitAll()

        selectedVersion.resourcesWithType(NATIVE).forEach { nativeResource ->
            extractZipFile(zipFilePath = nativeResource.location, outputPath = gameFolders.nativesDir)
        }

        selectedVersion.assetIndexResource?.let { assetIndexResource ->
            val file = downloadIfNotExists(assetIndexResource).await()
            val assetIndexFile = json.decodeFromString<AssetIndexFile>(file.decodeToString())

            val asyncDownloads =
                assetIndexFile.objects.map { (_, asset) ->
                    val resourceFile = asset.toResourceFile(gameFolders, MOJANG_REPOSITORY_URL)
                    downloadIfNotExists(resourceFile)
                }

            asyncDownloads.awaitAll()
        }

        selectedVersion.resourceWithType(SERVER_DATA)?.let { extractServerData(it) }
    }

    suspend fun clearDownloadFlow() {
        downloadFlow.emit(DownloadProgress("", 1.0))
    }

    private suspend fun loadMinecraftVersion(selectedVersion: Version) {
        val versionInfoResource = requireNotNull(selectedVersion.versionInfoResource)
        val versionInfo =
            downloadIfNotExists(versionInfoResource).await()
                .let { versionInfo -> json.decodeFromString<MojangVersion>(versionInfo.decodeToString()) }

        val mappedMinecraftVersion =
            MappedMinecraftVersion.fromMojangVersion(
                mojangVersion = versionInfo,
                platformType = platformData.platformType,
                arch = platformData.arch,
            ).also { this.selectedMinecraftVersion = it }

        selectedVersion.loadMinecraftVersionResources(mappedMinecraftVersion, gameFolders)
    }

    private suspend fun loadForgeVersion(
        selectedVersion: Version,
        forgeVersionInfoResource: ResourceFile,
    ) {
        val forgeVersionInfo =
            downloadIfNotExists(forgeVersionInfoResource).await()
                .let { forgeVersionInfo ->
                    json.decodeFromString<ForgeVersion>(forgeVersionInfo.decodeToString())
                }

        val mappedForgeVersion =
            MappedForgeVersion.fromForgeVersion(forgeVersionInfo)
                .also { this.selectedForgeVersion = it }

        selectedVersion.loadForgeVersionResources(mappedForgeVersion, gameFolders)
    }

    private suspend fun extractServerData(resourceFile: ResourceFile) {
        val alreadyDownloadedServerData = fileExists(resourceFile.location)
        if (!alreadyDownloadedServerData) {
            downloadIfNotExists(resourceFile).await()
            extractZipFile(zipFilePath = resourceFile.location, outputPath = gameFolders.gameDir)
        }
    }

    private fun downloadIfNotExists(resourceFile: ResourceFile): Deferred<ByteArray> {
        return downloadIfNotExists(location = resourceFile.location, remoteUrl = resourceFile.remoteUrl)
    }

    private fun downloadIfNotExists(
        location: String,
        remoteUrl: String,
    ): Deferred<ByteArray> {
        return coroutineScope.async(coroutineDispatcher) {
            semaphore.withPermit {
                if (fileExists(location)) {
                    loadFile(location)
                } else {
                    val downloadedFile =
                        fileDownloader.download(remoteUrl, onDownloadProgress = { downloadFlow.emit(it) })
                    saveIntoFile(location, downloadedFile)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_TIMEOUT_MILLIS = 30_000L

        fun start(
            json: Json,
            launcherFolderName: String = "MinecraftLauncher_Data",
            versionsFileUrl: String = DEFAULT_VERSIONS_FILE_URL,
        ): Launcher {
            val platformData = getPlatformData()

            val gameFolders =
                GameFolders(baseDir = platformData.appDataDir withSeparator launcherFolderName).also { it.createDefaultFolders() }

            return Launcher(
                platformData = platformData,
                gameFolders = gameFolders,
                json = json,
                versionsFileUrl = versionsFileUrl,
            )
        }

        private fun createHttpClient(): HttpClient {
            return HttpClient(CIO) {
                install(HttpTimeout) {
                    requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
                }
            }
        }
    }
}
