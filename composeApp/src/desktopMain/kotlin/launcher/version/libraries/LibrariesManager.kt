package launcher.version.libraries

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import launcher.config.LauncherConfig
import launcher.util.downloadFile
import launcher.util.fileExists
import launcher.version.data.VersionData
import launcher.version.json.MojangVersionLoader
import java.io.File
import java.net.URI
import java.util.zip.ZipFile

typealias LibraryPath = String

object LibrariesManager {
    suspend fun loadVersion(config: LauncherConfig): Pair<VersionData, List<LibraryPath>> {
        val mojangVersion = MojangVersionLoader.load(Json { ignoreUnknownKeys = true }, config.versionFilePath)
        val versionData = VersionDataExtractor.extract(mojangVersion, config.platform, config.arch)

        createDefaultFolders(config)

        val clientPath = downloadClient(config, versionData)
        val librariesPath = downloadLibraries(config, versionData)

        extractNativeLibraries(config, versionData)

        return versionData to (librariesPath + clientPath)
    }

    private fun createDefaultFolders(config: LauncherConfig) {
        File(config.gameDir).mkdirs()
        File(config.assetsDir).mkdirs()
        File(config.librariesDir).mkdirs()
        File(config.versionsDir).mkdirs()
        File(config.nativesDir).mkdirs()
    }

    private suspend fun downloadClient(
        config: LauncherConfig,
        versionData: VersionData,
    ): LibraryPath {
        return downloadLibrary(
            config,
            versionId = versionData.versionId,
            url = versionData.versionLibrary.url,
            isClient = true,
        )
    }

    private suspend fun downloadLibraries(
        config: LauncherConfig,
        versionData: VersionData,
    ): List<LibraryPath> {
        return versionData.libraries.map { library ->
            downloadLibrary(
                config = config,
                versionId = versionData.versionId,
                url = library.url,
                isClient = false,
            )
        }
    }

    private suspend fun extractNativeLibraries(
        config: LauncherConfig,
        versionData: VersionData,
    ) {
        val nativeLibraries = versionData.libraries.filter { it.isNative }

        nativeLibraries.forEach { library ->
            val jarFilePath = getLibraryPath(config, library.url, versionId = versionData.versionId, isClient = false)
            extractZipFile(zipFilePath = jarFilePath, outputPath = config.nativesDir)
        }
    }

    private suspend fun extractZipFile(
        zipFilePath: String,
        outputPath: String,
    ) {
        withContext(Dispatchers.IO) {
            ZipFile(zipFilePath).use { zipFile ->
                zipFile.entries().asSequence().forEach { zipFileEntry ->
                    zipFile.getInputStream(zipFileEntry).use { entryInputStream ->
                        val entryPath = outputPath + File.separator + zipFileEntry.name
                        if (!zipFileEntry.isDirectory) {
                            File(entryPath).writeBytes(entryInputStream.readAllBytes())
                        } else {
                            File(entryPath).mkdirs()
                        }
                    }
                }
            }
        }
    }

    private suspend fun downloadLibrary(
        config: LauncherConfig,
        versionId: String,
        url: String,
        isClient: Boolean,
    ): LibraryPath {
        val libraryPath =
            getLibraryPath(
                config,
                url = url,
                versionId = versionId,
                isClient = isClient,
            )

        if (!fileExists(path = libraryPath)) {
            download(url, libraryPath)
        }

        return libraryPath
    }

    private fun getLibraryPath(
        config: LauncherConfig,
        url: String,
        versionId: String,
        isClient: Boolean,
    ): String {
        val prefix = if (isClient) config.versionsDir else config.librariesDir
        val urlSuffix = URI(url).path

        return if (isClient) {
            "$prefix/$versionId/$versionId.jar"
        } else {
            "$prefix/$urlSuffix"
        }
    }

    private suspend fun download(
        url: String,
        libraryPath: String,
    ) {
        val response = downloadFile(url)

        File(libraryPath).let { file ->
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            file.writeBytes(response)
        }
    }
}
