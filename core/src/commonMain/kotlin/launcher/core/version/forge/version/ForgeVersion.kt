package launcher.core.version.forge.version

import kotlinx.serialization.Serializable

@Serializable
data class ForgeVersion(
    val id: String,
    val minecraftArguments: String,
    val mainClass: String,
    val assets: String,
    val libraries: List<ForgeVersionLibrary>,
)

@Serializable
data class ForgeVersionLibrary(
    val name: String,
    val url: String? = null,
    val serverreq: Boolean? = null,
    val clientreq: Boolean? = null,
)
