package launcher.core

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.flow.MutableSharedFlow
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
private const val DEFAULT_VERSIONS_FILE_URL = "https://launcher-server.fly.dev/versions.json"

class Launcher private constructor(
    val platformData: PlatformData,
    val gameFolders: GameFolders,
    val json: Json,
    val versionsFileUrl: String,
) {
    private val fileDownloader = FileDownloaderAdapter(HttpClient(CIO))

    var selectedVersion: Version? = null
    var selectedMinecraftVersion: MappedMinecraftVersion? = null
    var selectedForgeVersion: MappedForgeVersion? = null

    val downloadFlow = MutableSharedFlow<DownloadProgress>()

    suspend fun downloadVersionsFile() {
        val file =
            downloadIfNotExists(
                location = gameFolders.baseDir withSeparator "versions.json",
                remoteUrl = versionsFileUrl,
            )
        val versionConfig = json.decodeFromString<VersionConfig>(file.decodeToString())
        selectedVersion = versionConfig.versions.first().toVersion(gameFolders)
    }

    suspend fun downloadResources() {
        val selectedVersion = requireNotNull(selectedVersion)

        loadMinecraftVersion(selectedVersion)
        selectedVersion.forgeVersionInfoResource?.let { loadForgeVersion(selectedVersion, it) }

        selectedVersion.resources.filter { it.type != SERVER_DATA }
            .forEach { resource -> downloadIfNotExists(resource) }

        selectedVersion.resourcesWithType(NATIVE).forEach { nativeResource ->
            extractZipFile(zipFilePath = nativeResource.location, outputPath = gameFolders.nativesDir)
        }

        selectedVersion.assetIndexResource?.let { assetIndexResource ->
            val file = downloadIfNotExists(assetIndexResource)
            val assetIndexFile = json.decodeFromString<AssetIndexFile>(file.decodeToString())
            assetIndexFile.objects.forEach { (_, asset) ->
                val resourceFile = asset.toResourceFile(gameFolders, MOJANG_REPOSITORY_URL)
                downloadIfNotExists(resourceFile)
            }
        }

        selectedVersion.resourceWithType(SERVER_DATA)?.let { extractServerData(it) }
    }

    private suspend fun loadMinecraftVersion(selectedVersion: Version) {
        val versionInfoResource = requireNotNull(selectedVersion.versionInfoResource)
        val versionInfo =
            downloadIfNotExists(versionInfoResource)
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
            downloadIfNotExists(forgeVersionInfoResource)
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
            downloadIfNotExists(resourceFile)
            extractZipFile(zipFilePath = resourceFile.location, outputPath = gameFolders.gameDir)
        }
    }

    private suspend fun downloadIfNotExists(resourceFile: ResourceFile): ByteArray {
        return downloadIfNotExists(location = resourceFile.location, remoteUrl = resourceFile.remoteUrl)
    }

    private suspend fun downloadIfNotExists(
        location: String,
        remoteUrl: String,
    ): ByteArray {
        return if (fileExists(location)) {
            loadFile(location)
        } else {
            val downloadedFile = fileDownloader.download(remoteUrl, onDownloadProgress = { downloadFlow.emit(it) })
            saveIntoFile(location, downloadedFile)
        }
    }

    companion object {
        fun start(
            json: Json,
            launcherFolderName: String = getCurrentPath() withSeparator "MinecraftLauncher_Data",
            versionsFileUrl: String = DEFAULT_VERSIONS_FILE_URL,
        ): Launcher {
            val platformData = getPlatformData()

            val gameFolders = GameFolders(baseDir = launcherFolderName).also { it.createDefaultFolders() }

            return Launcher(
                platformData = platformData,
                gameFolders = gameFolders,
                json = json,
                versionsFileUrl = versionsFileUrl,
            )
        }
    }
}
