package launcher.core.file

data class ResourceFile(
    val type: ResourceFileType,
    val remoteUrl: String,
    val location: String,
)

enum class ResourceFileType {
    VERSION_INFO,
    VERSION,
    LIBRARY,
    NATIVE,
    ASSET_INDEX,
    ASSET,
    SERVER_DATA,
    ;

    fun getBaseFolder(gameFolders: GameFolders): String {
        return when (this) {
            VERSION_INFO, VERSION -> gameFolders.versionsDir
            LIBRARY -> gameFolders.librariesDir
            SERVER_DATA -> gameFolders.cacheDir
            NATIVE -> gameFolders.nativesCacheDir
            ASSET, ASSET_INDEX -> gameFolders.assetsDir
        }
    }
}
