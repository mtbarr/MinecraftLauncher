package launcher.version.json

import kotlinx.serialization.Serializable

@Serializable
data class MojangVersionLibrary(
    val name: String,
    val downloads: MojangVersionLibraryDownloads,
    val rules: List<MojangVersionLibraryRule> = emptyList(),
    val natives: Map<String, String> = emptyMap()
)

@Serializable
data class MojangVersionLibraryDownloads(
    val artifact: MojangVersionLibraryArtifact? = null,
    val classifiers: Map<String, MojangVersionLibraryClassifier> = emptyMap()
)

@Serializable
data class MojangVersionLibraryArtifact(
    val sha1: String,
    val size: Int,
    val url: String
)

@Serializable
data class MojangVersionLibraryClassifier(
    val path: String,
    val sha1: String,
    val size: Int,
    val url: String
)

@Serializable
data class MojangVersionLibraryRule(
    val action: String,
    val os: MojangVersionLibraryRuleOS? = null
)

@Serializable
data class MojangVersionLibraryRuleOS(
    val name: String
)
