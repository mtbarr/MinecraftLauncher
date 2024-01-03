package launcher.util

import java.io.File

fun fileExists(path: String): Boolean {
    val size = File(path).length()
    return size > 0L
}

fun saveIntoFile(
    path: String,
    bytes: ByteArray,
): File {
    return File(path).also { file ->
        file.parentFile.let { parentFile ->
            if (!parentFile.exists()) parentFile.mkdirs()
        }
        file.writeBytes(bytes)
    }
}
