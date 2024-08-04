package launcher.core.version.minecraft

import launcher.core.Arch
import launcher.core.PlatformType
import launcher.core.version.minecraft.version.MojangVersion
import launcher.core.version.minecraft.version.MojangVersionLibrary

class MappedMinecraftVersion(
    val versionId: String,
    val versionLibrary: MappedMinecraftVersionLibrary,
    val assetIndexId: String,
    val assetIndexUrl: String,
    val mainClass: String,
    val libraries: List<MappedMinecraftVersionLibrary>,
    val minecraftArguments: String,
) {
    companion object {
        private const val ALLOW = "allow"

        fun fromMojangVersion(
            mojangVersion: MojangVersion,
            platformType: PlatformType,
            arch: String,
        ): MappedMinecraftVersion {
            val filteredLibrariesByPlatform =
                mojangVersion.libraries.filter { library ->
                    val hasRules = library.rules.isNotEmpty()
                    val platformRules =
                        library.rules
                            .firstOrNull { rule -> rule.os?.name.equals(platformType.nativeId, true) }
                            ?: library.rules.firstOrNull { rule -> rule.os == null }

                    val isPlatformAllowed = platformRules?.action == ALLOW

                    !hasRules || isPlatformAllowed
                }
            val libraries =
                filteredLibrariesByPlatform.map {
                    MappedMinecraftVersionLibrary.selectPlatformLibrary(it, platformType, arch)
                }

            return MappedMinecraftVersion(
                versionId = mojangVersion.id,
                mainClass = mojangVersion.mainClass,
                assetIndexId = mojangVersion.assetIndex.id,
                assetIndexUrl = mojangVersion.assetIndex.url,
                versionLibrary =
                    mojangVersion.downloads["client"]!!.let {
                        MappedMinecraftVersionLibrary(
                            sha1 = it.sha1,
                            size = it.size,
                            url = it.url,
                        )
                    },
                libraries = libraries,
                minecraftArguments = mojangVersion.minecraftArguments,
            )
        }
    }
}

data class MappedMinecraftVersionLibrary(
    val sha1: String,
    val size: Int,
    val url: String,
    val isNative: Boolean = false,
) {
    companion object {
        fun selectPlatformLibrary(
            mojangLibrary: MojangVersionLibrary,
            platformType: PlatformType,
            arch: Arch,
        ): MappedMinecraftVersionLibrary {
            val nativeClassifierName = mojangLibrary.natives[platformType.nativeId]?.replace("\${arch}", arch)

            return if (nativeClassifierName != null) {
                val classifier =
                    mojangLibrary.downloads.classifiers[nativeClassifierName]
                        ?: error("Classifier not found for classifierName [$nativeClassifierName] for mojang library [$mojangLibrary]")

                with(classifier) {
                    MappedMinecraftVersionLibrary(
                        sha1 = sha1,
                        size = size,
                        url = url,
                        isNative = true,
                    )
                }
            } else {
                val artifact =
                    mojangLibrary.downloads.artifact
                        ?: error("artifact not found for mojangLibrary [$mojangLibrary]")

                with(artifact) {
                    MappedMinecraftVersionLibrary(
                        sha1 = sha1,
                        size = size,
                        url = url,
                        isNative = false,
                    )
                }
            }
        }
    }
}
