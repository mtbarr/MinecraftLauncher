package launcher.version.json

import kotlinx.serialization.Serializable

@Serializable
data class MojangVersion(
    val id: String,
    val assets: String,
    val assetIndex: MojangVersionAssetIndex,
    val downloads: Map<String, MojangVersionArtifact>,
    val javaVersion: MojangVersionJavaVersion,
    val mainClass: String,
    val minecraftArguments: String,
    val libraries: List<MojangVersionLibrary>
)

@Serializable
data class MojangVersionAssetIndex(
    val id: String,
    val sha1: String,
    val size: Int,
    val totalSize: Int,
    val url: String
)

@Serializable
data class MojangVersionArtifact(
    val sha1: String,
    val size: Int,
    val url: String
)

@Serializable
data class MojangVersionJavaVersion(
    val component: String,
    val majorVersion: Int
)