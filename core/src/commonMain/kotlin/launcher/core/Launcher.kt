package launcher.core

import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.json.Json
import launcher.core.extensions.withSeparator
import launcher.core.file.GameFolders
import launcher.core.file.ResourceFile
import launcher.core.file.ResourceFileType.NATIVE
import launcher.core.file.download.DownloadProgress
import launcher.core.file.download.FileDownloader
import launcher.core.file.download.FileDownloaderAdapter
import launcher.core.version.Version
import launcher.core.version.minecraft.MappedMinecraftVersion
import launcher.core.version.minecraft.assets.AssetIndexFile
import launcher.core.version.minecraft.version.MojangVersion

private const val LAUNCHER_FOLDER_NAME = ".launcher2"
private const val MOJANG_REPOSITORY_URL = "https://resources.download.minecraft.net"

class Launcher private constructor(
    val platformData: PlatformData,
    val gameFolders: GameFolders,
    val json: Json,
) {
    var selectedVersion: Version? = null
    var selectedMinecraftVersion: MappedMinecraftVersion? = null

    val downloadFlow = MutableSharedFlow<DownloadProgress>()

    suspend fun downloadResources() {
        val selectedVersion = requireNotNull(selectedVersion)
        val fileDownloader = FileDownloaderAdapter(HttpClient())

        val versionInfoResource = requireNotNull(selectedVersion.versionInfoResource)
        val versionInfo =
            versionInfoResource.downloadIfNotExists(fileDownloader)
                .let { versionInfo -> json.decodeFromString<MojangVersion>(String(versionInfo)) }

        val mappedMinecraftVersion =
            MappedMinecraftVersion.fromMojangVersion(
                mojangVersion = versionInfo,
                platform = platformData.platform,
                arch = platformData.arch,
            ).also { this.selectedMinecraftVersion = it }

        selectedVersion.loadMinecraftVersionResources(mappedMinecraftVersion, gameFolders)
        selectedVersion.resources.forEach { resource -> resource.downloadIfNotExists(fileDownloader) }

        selectedVersion.resourcesWithType(NATIVE).forEach { nativeResource ->
            extractZipFile(zipFilePath = nativeResource.location, outputPath = gameFolders.nativesDir)
        }

        selectedVersion.assetIndexResource?.let { assetIndexResource ->
            val file = assetIndexResource.downloadIfNotExists(fileDownloader)
            val assetIndexFile = json.decodeFromString<AssetIndexFile>(file.decodeToString())
            assetIndexFile.objects.forEach { (_, asset) ->
                asset.toResourceFile(gameFolders, MOJANG_REPOSITORY_URL).downloadIfNotExists(fileDownloader)
            }
        }
    }

    private suspend fun ResourceFile.downloadIfNotExists(fileDownloader: FileDownloader): ByteArray {
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
            )
        }
    }
}
