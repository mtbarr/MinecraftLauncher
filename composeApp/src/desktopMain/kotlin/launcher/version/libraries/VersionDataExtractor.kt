package launcher.version.libraries

import launcher.version.json.MojangVersionLibrary
import kotlinx.serialization.Serializable
import launcher.config.Arch
import launcher.config.Platform
import launcher.version.data.Library
import launcher.version.data.VersionData
import launcher.version.json.MojangVersion

private const val ALLOW = "allow"

object VersionDataExtractor {
    fun extract(mojangVersion: MojangVersion, platform: Platform, arch: String): VersionData {
        val filteredLibrariesByPlatform = mojangVersion.libraries.filter { library ->
            val hasRules = library.rules.isNotEmpty()
            val platformRules = library.rules
                .firstOrNull { rule -> rule.os?.name == platform.nativeId }
                ?: library.rules.firstOrNull { rule -> rule.os == null }

            val isPlatformAllowed = platformRules?.action == ALLOW

            !hasRules || isPlatformAllowed
        }
        val libraries = filteredLibrariesByPlatform.map { selectPlatformLibrary(it, platform, arch) }

        return VersionData(
            versionId = mojangVersion.id,
            mainClass = mojangVersion.mainClass,
            assetIndexId = mojangVersion.assetIndex.id,
            versionLibrary = mojangVersion.downloads["client"]!!.let {
                Library(
                    sha1 = it.sha1,
                    size = it.size,
                    url = it.url
                )
            },
            libraries = libraries,
            minecraftArguments = mojangVersion.minecraftArguments
        )
    }

    private fun selectPlatformLibrary(mojangLibrary: MojangVersionLibrary, platform: Platform, arch: Arch): Library {
        val nativeClassifierName = mojangLibrary.natives[platform.nativeId]?.replace("\${arch}", arch)

        return if (nativeClassifierName != null) {
            val classifier = mojangLibrary.downloads.classifiers[nativeClassifierName]
                ?: error("Classifier not found for classifierName [$nativeClassifierName] for mojang library [$mojangLibrary]")

            with(classifier) {
                Library(
                    sha1 = sha1,
                    size = size,
                    url = url,
                    isNative = true
                )
            }
        } else {
            val artifact = mojangLibrary.downloads.artifact
                ?: error("artifact not found for mojangLibrary [$mojangLibrary]")

            with(artifact) {
                Library(
                    sha1 = sha1,
                    size = size,
                    url = url,
                    isNative = false
                )
            }
        }
    }
}
