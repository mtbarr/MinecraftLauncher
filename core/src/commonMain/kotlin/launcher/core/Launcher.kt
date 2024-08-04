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
import launcher.core.file.download.DownloadProgress
import launcher.core.file.download.FileDownloaderAdapter
import launcher.core.version.Version
import launcher.core.version.minecraft.MappedMinecraftVersion
import launcher.core.version.minecraft.assets.AssetIndexFile
import launcher.core.version.minecraft.version.MojangVersion

private const val LAUNCHER_FOLDER_NAME = ".launcher2"
private const val MOJANG_REPOSITORY_URL = "https://resources.download.minecraft.net"
private const val DEFAULT_VERSIONS_FILE_URL = "http://localhost:8081/versions.json"

class Launcher private constructor(
    val platformData: PlatformData,
    val gameFolders: GameFolders,
    val json: Json,
    val versionsFileUrl: String,
) {
    private val fileDownloader = FileDownloaderAdapter(HttpClient(CIO))

    var selectedVersion: Version? = null
    var selectedMinecraftVersion: MappedMinecraftVersion? = null

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
        selectedVersion.resources.forEach { resource -> downloadIfNotExists(resource) }

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
            launcherFolderName: String = LAUNCHER_FOLDER_NAME,
            versionsFileUrl: String = DEFAULT_VERSIONS_FILE_URL,
        ): Launcher {
            val platformData = getPlatformData()

            val gameFolders =
                GameFolders(
                    baseDir = platformData.appDataDir withSeparator launcherFolderName,
                ).also { it.createDefaultFolders() }

            return Launcher(
                platformData = platformData,
                gameFolders = gameFolders,
                json = json,
                versionsFileUrl = versionsFileUrl,
            )
        }
    }
}
