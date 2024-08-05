package launcher.core.version

import launcher.core.version.forge.MappedForgeVersion
import launcher.core.version.minecraft.MappedMinecraftVersion

data class VersionRunnerData(
    val mainClass: String,
    val minecraftArguments: String,
    val versionId: String,
    val assetIndexId: String,
    val serverAddress: String? = null,
) {
    companion object {
        fun create(
            minecraftVersion: MappedMinecraftVersion,
            forgeVersion: MappedForgeVersion?,
            serverAddress: String?,
        ): VersionRunnerData {
            return if (forgeVersion != null) {
                VersionRunnerData(
                    mainClass = forgeVersion.mainClass,
                    minecraftArguments = forgeVersion.minecraftArguments,
                    versionId = forgeVersion.versionId,
                    assetIndexId = forgeVersion.assetIndexId,
                    serverAddress = serverAddress,
                )
            } else {
                VersionRunnerData(
                    mainClass = minecraftVersion.mainClass,
                    minecraftArguments = minecraftVersion.minecraftArguments,
                    versionId = minecraftVersion.versionId,
                    assetIndexId = minecraftVersion.assetIndexId,
                    serverAddress = serverAddress,
                )
            }
        }
    }
}
