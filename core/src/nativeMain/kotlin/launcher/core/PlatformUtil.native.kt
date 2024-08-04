@file:OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)

package launcher.core

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import platform.posix.getenv
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.OsFamily.LINUX
import kotlin.native.OsFamily.MACOSX
import kotlin.native.OsFamily.WINDOWS

actual fun getPlatformData(): PlatformData {
    val platformType =
        when (Platform.osFamily) {
            MACOSX -> PlatformType.OSX
            WINDOWS -> PlatformType.WINDOWS
            LINUX -> PlatformType.LINUX
            else -> error("Unsupported platform: ${Platform.osFamily}")
        }
    val arch = Platform.cpuArchitecture.bitness.toString()

    return PlatformData(platformType = platformType, arch = arch, appDataDir = getAppDataDir())
}

actual fun fileExists(path: String): Boolean {
    return FileSystem.SYSTEM.exists(path.toPath())
}

actual fun saveIntoFile(
    path: String,
    bytes: ByteArray,
): ByteArray {
    FileSystem.SYSTEM.sink(path.toPath()).buffer().use { sink ->
        sink.write(bytes)
    }
    return bytes
}

actual fun loadFile(path: String): ByteArray {
    return FileSystem.SYSTEM.source(path.toPath()).buffer().use { source ->
        source.readByteArray()
    }
}

private fun getAppDataDir(): String {
    val home = getenv("HOME")?.toKString() ?: error("Unable to get home directory")

    return when (Platform.osFamily) {
        WINDOWS -> "$home\\AppData\\Roaming"
        else -> home
    }
}

actual inline fun <reified T> loadJsonFile(
    json: Json,
    path: String,
): T {
    val file = loadFile(path)
    return json.decodeFromString<T>(file.decodeToString())
}

actual suspend fun extractZipFile(
    zipFilePath: String,
    outputPath: String,
) {
//    withContext(Dispatchers.IO) {
//        ZipFile(zipFilePath).use { zipFile ->
//            zipFile.entries().asSequence()
//                .filter { zipFileEntry -> !zipFileEntry.name.contains("META-INF") }
//                .forEach { zipFileEntry ->
//                    zipFile.getInputStream(zipFileEntry).use { entryInputStream ->
//                        val entryPath = outputPath + File.separator + zipFileEntry.name
//                        if (!zipFileEntry.isDirectory) {
//                            File(entryPath).writeBytes(entryInputStream.readAllBytes())
//                        } else {
//                            File(entryPath).mkdirs()
//                        }
//                    }
//                }
//        }
//    }
}

actual fun fileSeparator(): String =
    when (Platform.osFamily) {
        MACOSX, LINUX -> "/"
        WINDOWS -> "\\"
        else -> error("Unsupported platform: ${Platform.osFamily}")
    }

actual fun pathSeparator(): String =
    when (Platform.osFamily) {
        MACOSX, LINUX -> ":"
        WINDOWS -> ";"
        else -> error("Unsupported platform: ${Platform.osFamily}")
    }

actual fun extractUrlPath(url: String): String {
    val index = url.indexOf(char = '/', startIndex = url.indexOf("://") + 3)
    return if (index == -1) "" else url.substring(index)
}

actual fun createFolder(path: String) {
    FileSystem.SYSTEM.createDirectories(path.toPath(), mustCreate = false)
}
