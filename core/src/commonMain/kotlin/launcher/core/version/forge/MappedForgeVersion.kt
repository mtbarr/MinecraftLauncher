package launcher.core.version.forge

import launcher.core.version.forge.version.ForgeVersion
import launcher.core.version.forge.version.ForgeVersionLibrary

class MappedForgeVersion(
    val versionId: String,
    val versionLibrary: MappedForgeVersionLibrary,
    val assetIndexId: String,
    val assetIndexUrl: String,
    val mainClass: String,
    val libraries: List<MappedForgeVersionLibrary>,
    val minecraftArguments: String,
) {
    companion object {
        fun fromForgeVersion(forgeVersion: ForgeVersion): MappedForgeVersion {
            val versionLibrary = forgeVersion.libraries.first()
            val libraries = forgeVersion.libraries.drop(1)

            return MappedForgeVersion(
                versionId = forgeVersion.id,
                mainClass = forgeVersion.mainClass,
                assetIndexId = forgeVersion.assets,
                assetIndexUrl = forgeVersion.assets,
                versionLibrary = MappedForgeVersionLibrary.fromForgeVersionLibrary(versionLibrary, isClient = true),
                libraries = libraries.map(MappedForgeVersionLibrary::fromForgeVersionLibrary),
                minecraftArguments = forgeVersion.minecraftArguments,
            )
        }
    }
}

class MappedForgeVersionLibrary(
    val url: String,
    val prefix: String,
) {
    companion object {
        fun fromForgeVersionLibrary(
            forgeVersionLibrary: ForgeVersionLibrary,
            isClient: Boolean = false,
        ): MappedForgeVersionLibrary {
            val prefix = forgeVersionLibrary.url ?: "https://libraries.minecraft.net/"
            val url = createUrl(prefix = prefix, name = forgeVersionLibrary.name, isClient)

            return MappedForgeVersionLibrary(prefix = prefix, url = url)
        }

        private fun createUrl(
            prefix: String,
            name: String,
            isClient: Boolean,
        ): String {
            val (group, artifact, version) = name.split(":")
            val fileName = if (isClient) "$artifact-$version-universal.jar" else "$artifact-$version.jar"

            return "$prefix${group.replace(".", "/")}/$artifact/$version/$fileName"
        }
    }
}
