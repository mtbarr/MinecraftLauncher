package launcher.core.file

import launcher.core.createFolder
import launcher.core.extensions.withSeparator

class GameFolders(val baseDir: String) {
    val gameDir = baseDir withSeparator GAME_SUFFIX
    val assetsDir = baseDir withSeparator ASSETS_SUFFIX
    val librariesDir = baseDir withSeparator LIBRARIES_SUFFIX
    val versionsDir = baseDir withSeparator VERSIONS_SUFFIX
    val nativesDir = baseDir withSeparator NATIVES_SUFFIX
    val cacheDir = baseDir withSeparator CACHE_SUFFIX
    val nativesCacheDir = baseDir withSeparator CACHE_SUFFIX withSeparator NATIVES_SUFFIX

    fun createDefaultFolders() {
        allDirs().forEach { dir -> createFolder(dir) }
    }

    private fun allDirs() =
        listOf(
            gameDir,
            assetsDir,
            librariesDir,
            versionsDir,
            nativesDir,
            cacheDir,
            nativesCacheDir,
        )

    private companion object {
        const val GAME_SUFFIX = "game"
        const val ASSETS_SUFFIX = "assets"
        const val LIBRARIES_SUFFIX = "libraries"
        const val VERSIONS_SUFFIX = "versions"
        const val NATIVES_SUFFIX = "natives"
        const val CACHE_SUFFIX = "cache"
    }
}
