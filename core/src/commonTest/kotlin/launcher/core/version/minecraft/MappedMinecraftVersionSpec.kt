package launcher.core.version.minecraft

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import launcher.core.PlatformType.LINUX
import launcher.core.PlatformType.OSX
import launcher.core.PlatformType.WINDOWS
import launcher.core.version.minecraft.version.MojangVersion
import launcher.core.version.minecraft.version.MojangVersionArtifact
import launcher.core.version.minecraft.version.MojangVersionAssetIndex
import launcher.core.version.minecraft.version.MojangVersionLibrary
import launcher.core.version.minecraft.version.MojangVersionLibraryArtifact
import launcher.core.version.minecraft.version.MojangVersionLibraryDownloads
import launcher.core.version.minecraft.version.MojangVersionLibraryRule
import launcher.core.version.minecraft.version.MojangVersionLibraryRuleOS

class MappedMinecraftVersionSpec : DescribeSpec({
    describe("artifact without rules") {
        withData(
            nameFn = { (platform, arch) -> "should return same result for all platforms and arch [$platform $arch]" },
            LINUX to "32",
            LINUX to "64",
            WINDOWS to "32",
            WINDOWS to "64",
            OSX to "32",
            OSX to "64",
        ) { (platform, arch) ->
            val libraries = versionWithLibraryWithoutRules()
            MappedMinecraftVersion.fromMojangVersion(mojangVersionWithLibraries(libraries), platform, arch)
                .should { mappedVersion ->
                    mappedVersion.libraries
                        .shouldHaveSize(1)
                        .first().should { library ->
                            library.sha1 shouldBe "0a4b8a9a4a654b84a"
                            library.size shouldBe 15966
                            library.url shouldBe "http://localhost/com/lib/1.8.8/lib-1.8.8.jar"
                        }
                }
        }
    }

    describe("artifact disallow OSX") {
        withData(
            nameFn = { (platform, arch) -> " should return same result for all platforms except OSX [$platform $arch]" },
            LINUX to "32",
            LINUX to "64",
            WINDOWS to "32",
            WINDOWS to "64",
            OSX to "32",
            OSX to "64",
        ) { (platform, arch) ->
            val libraries = versionWithLibraryWithDisallowOSX()
            val result =
                MappedMinecraftVersion
                    .fromMojangVersion(mojangVersionWithLibraries(libraries), platform, arch)

            if (platform == OSX) {
                println("result.libraries = ${result.libraries}")
                result.libraries.shouldBeEmpty()
            } else {
                result.libraries.shouldHaveSize(1)
                    .first().should { library ->
                        library.sha1 shouldBe "0a4b8a9a4a654b84a"
                        library.size shouldBe 15966
                        library.url shouldBe "http://localhost/com/lib/1.8.8/lib-1.8.8.jar"
                    }
            }
        }
    }
}) {
    private companion object {
        fun mojangVersionWithLibraries(libraries: List<MojangVersionLibrary>): MojangVersion {
            return MojangVersion(
                id = "1.7.10",
                assets = "1.7.10",
                assetIndex =
                    MojangVersionAssetIndex(
                        id = "1.7.10",
                        sha1 = "sha1",
                        size = 10000,
                        totalSize = 10000,
                        url = "assetIndexUrl",
                    ),
                downloads =
                    mapOf(
                        "client" to
                            MojangVersionArtifact(
                                sha1 = "0a4b8a9a4a654b84a",
                                size = 15966,
                                url = "http://localhost/com/lib/1.8.8/lib-1.8.8.jar",
                            ),
                    ),
                javaVersion = null,
                mainClass = "",
                minecraftArguments = "",
                libraries = libraries,
            )
        }

        fun versionWithLibraryWithoutRules(): List<MojangVersionLibrary> =
            listOf(
                MojangVersionLibrary(
                    name = "name",
                    downloads =
                        MojangVersionLibraryDownloads(
                            artifact =
                                MojangVersionLibraryArtifact(
                                    sha1 = "0a4b8a9a4a654b84a",
                                    size = 15966,
                                    url = "http://localhost/com/lib/1.8.8/lib-1.8.8.jar",
                                ),
                        ),
                ),
            )

        fun versionWithLibraryWithDisallowOSX(): List<MojangVersionLibrary> =
            listOf(
                MojangVersionLibrary(
                    name = "name",
                    downloads =
                        MojangVersionLibraryDownloads(
                            artifact =
                                MojangVersionLibraryArtifact(
                                    sha1 = "0a4b8a9a4a654b84a",
                                    size = 15966,
                                    url = "http://localhost/com/lib/1.8.8/lib-1.8.8.jar",
                                ),
                        ),
                    rules =
                        listOf(
                            MojangVersionLibraryRule(action = "allow"),
                            MojangVersionLibraryRule(action = "disallow", os = MojangVersionLibraryRuleOS("OSX")),
                        ),
                ),
            )
    }
}
