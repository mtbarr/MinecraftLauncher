package launcher.core

import kotlinx.serialization.json.Json

typealias Arch = String

enum class PlatformType(val nativeId: String) {
    LINUX("linux"),
    OSX("osx"),
    WINDOWS("windows"),
}

data class PlatformData(
    val platformType: PlatformType,
    val arch: Arch,
    val appDataDir: String,
)

expect fun getPlatformData(): PlatformData

expect fun fileExists(path: String): Boolean

expect fun saveIntoFile(
    path: String,
    bytes: ByteArray,
): ByteArray

expect fun loadFile(path: String): ByteArray

expect inline fun <reified T> loadJsonFile(
    json: Json,
    path: String,
): T

expect suspend fun extractZipFile(
    zipFilePath: String,
    outputPath: String,
)

expect fun fileSeparator(): String

expect fun pathSeparator(): String

expect fun extractUrlPath(url: String): String

expect fun createFolder(path: String)

expect fun getCurrentPath(): String
