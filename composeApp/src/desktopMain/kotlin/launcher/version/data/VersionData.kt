package launcher.version.data

import kotlinx.serialization.Serializable

@Serializable
data class VersionData(
    val versionId: String,
    val versionLibrary: Library,
    val assetIndexId: String,
    val assetIndexUrl: String,
    val mainClass: String,
    val libraries: List<Library>,
    val minecraftArguments: String,
)

@Serializable
data class Library(
    val sha1: String,
    val size: Int,
    val url: String,
    val isNative: Boolean = false,
)
