package launcher.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util.zip.ZipFile

actual fun getPlatformData(): PlatformData {
    val osName = System.getProperty("os.name", "generic").lowercase()
    val platformType =
        when {
            osName.contains("mac") || osName.contains("darwin") -> PlatformType.OSX
            osName.contains("win") -> PlatformType.WINDOWS
            else -> PlatformType.LINUX
        }
    val arch =
        System.getProperty("os.arch", "x86").let { arch ->
            when {
                arch.contains("64") -> "64"
                arch.contains("32") || arch.contains("86") -> "32"
                else -> arch
            }
        }

    return PlatformData(platformType = platformType, arch = arch, appDataDir = getAppDataDir())
}

actual fun fileExists(path: String): Boolean {
    val size = File(path).length()
    return size > 0L
}

actual fun saveIntoFile(
    path: String,
    bytes: ByteArray,
): ByteArray {
    File(path).also { file ->
        file.parentFile.let { parentFile ->
            if (!parentFile.exists()) parentFile.mkdirs()
        }
        file.writeBytes(bytes)
    }
    return bytes
}

actual fun loadFile(path: String): ByteArray {
    return File(path).readBytes()
}

private fun getAppDataDir(): String {
    val home = System.getProperty("user.home")
    val separator = File.separator

    val appDataFolder = "$home${separator}AppData${separator}Roaming"

    return if (fileExists(appDataFolder)) {
        appDataFolder
    } else {
        home
    }
}

actual inline fun <reified T> loadJsonFile(
    json: Json,
    path: String,
): T {
    val file = loadFile(path)
    return json.decodeFromString<T>(String(file))
}

actual suspend fun extractZipFile(
    zipFilePath: String,
    outputPath: String,
) {
    withContext(Dispatchers.IO) {
        ZipFile(zipFilePath).use { zipFile ->
            zipFile.entries().asSequence()
                .filter { zipFileEntry -> !zipFileEntry.name.contains("META-INF") }
                .forEach { zipFileEntry ->
                    zipFile.getInputStream(zipFileEntry).use { entryInputStream ->
                        val entryPath = outputPath + File.separator + zipFileEntry.name
                        if (!zipFileEntry.isDirectory) {
                            File(entryPath).let { file ->
                                if (!file.exists()) {
                                    file.createNewFile()
                                }
                                file.writeBytes(entryInputStream.readAllBytes())
                            }
                        } else {
                            File(entryPath).mkdirs()
                        }
                    }
                }
        }
    }
}

actual fun fileSeparator(): String = File.separator

actual fun pathSeparator(): String = File.pathSeparator

actual fun extractUrlPath(url: String): String = URI(url).path

actual fun createFolder(path: String) {
    File(path).mkdirs()
}

actual fun getCurrentPath(): String {
    return Paths.get("").toAbsolutePath().toString()
}
