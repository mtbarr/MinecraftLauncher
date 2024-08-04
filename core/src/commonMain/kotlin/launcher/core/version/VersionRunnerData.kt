package launcher.core.version

import launcher.core.version.forge.MappedForgeVersion
import launcher.core.version.minecraft.MappedMinecraftVersion

data class VersionRunnerData(
    val mainClass: String,
    val minecraftArguments: String,
    val versionId: String,
    val assetIndexId: String,
) {
    companion object {
        fun create(
            minecraftVersion: MappedMinecraftVersion,
            forgeVersion: MappedForgeVersion?,
        ): VersionRunnerData {
            return if (forgeVersion != null) {
                VersionRunnerData(
                    mainClass = forgeVersion.mainClass,
                    minecraftArguments = forgeVersion.minecraftArguments,
                    versionId = forgeVersion.versionId,
                    assetIndexId = forgeVersion.assetIndexId,
                )
            } else {
                VersionRunnerData(
                    mainClass = minecraftVersion.mainClass,
                    minecraftArguments = minecraftVersion.minecraftArguments,
                    versionId = minecraftVersion.versionId,
                    assetIndexId = minecraftVersion.assetIndexId,
                )
            }
        }
    }
}
