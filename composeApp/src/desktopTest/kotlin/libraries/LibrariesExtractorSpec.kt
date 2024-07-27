package libraries

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.Test
import io.kotest.matchers.shouldBe

class Testes : FunSpec({
    @Test
    fun testA() {
        "a" shouldBe 1
    }
})
//
// class LibrariesExtractorSpec : DescribeSpec({
//    describe("artifact without rules") {
//        context("should return same result for all platforms and arch") {
//            val commonTest: (Platform, Arch) -> Unit = { platform, arch ->
//                val libraries = versionWithLibraryWithoutRules()
//                VersionDataExtractor.extract(libraries, platform, arch)
//                    .shouldHaveSize(1)
//                    .first().should { library ->
//                        library.sha1 shouldBe "0a4b8a9a4a654b84a"
//                        library.size shouldBe 15966
//                        library.url shouldBe "http://localhost/com/lib/1.8.8/lib-1.8.8.jar"
//                    }
//            }
//            it("Linux 32") { commonTest(LINUX, "32") }
//            it("Linux 64") { commonTest(LINUX, "64") }
//            it("Windows 32") { commonTest(WINDOWS, "32") }
//            it("Windows 64") { commonTest(WINDOWS, "64") }
//            it("OSX 32") { commonTest(OSX, "32") }
//            it("OSX 64") { commonTest(OSX, "64") }
//        }
//    }
//
//    describe("artifact disallow OSX") {
//        context("should return same result for all platforms except OSX") {
//            val givenPlatform: (Platform, Arch) -> List<Library> = { platform, arch ->
//                val libraries = versionWithLibraryWithDisallowOSX()
//                VersionDataExtractor.extract(libraries, platform, arch)
//            }
//
//            fun List<Library>.shouldReturnCommonResult() {
//                this.shouldHaveSize(1)
//                    .first().should { library ->
//                        library.sha1 shouldBe "0a4b8a9a4a654b84a"
//                        library.size shouldBe 15966
//                        library.url shouldBe "http://localhost/com/lib/1.8.8/lib-1.8.8.jar"
//                    }
//            }
//
//            fun List<Library>.shouldReturnEmptyResult() = this.shouldBeEmpty()
//
//            it("Linux 32") { givenPlatform(LINUX, "32").shouldReturnCommonResult() }
//            it("Linux 64") { givenPlatform(LINUX, "64").shouldReturnCommonResult() }
//            it("Windows 32") { givenPlatform(WINDOWS, "32").shouldReturnCommonResult() }
//            it("Windows 64") { givenPlatform(WINDOWS, "64").shouldReturnCommonResult() }
//            it("OSX 32") { givenPlatform(OSX, "32").shouldReturnEmptyResult() }
//            it("OSX 64") { givenPlatform(OSX, "64").shouldReturnCommonResult() }
//        }
//    }
// }) {
//    private companion object {
//        fun versionWithLibraryWithoutRules(): List<MojangVersionLibrary> =
//            listOf(
//                MojangVersionLibrary(
//                    name = "name",
//                    downloads =
//                        MojangVersionLibraryDownloads(
//                            artifact =
//                                MojangVersionLibraryArtifact(
//                                    sha1 = "0a4b8a9a4a654b84a",
//                                    size = 15966,
//                                    url = "http://localhost/com/lib/1.8.8/lib-1.8.8.jar",
//                                ),
//                        ),
//                ),
//            )
//
//        fun versionWithLibraryWithDisallowOSX(): List<MojangVersionLibrary> =
//            listOf(
//                MojangVersionLibrary(
//                    name = "name",
//                    downloads =
//                        MojangVersionLibraryDownloads(
//                            artifact =
//                                MojangVersionLibraryArtifact(
//                                    sha1 = "0a4b8a9a4a654b84a",
//                                    size = 15966,
//                                    url = "http://localhost/com/lib/1.8.8/lib-1.8.8.jar",
//                                ),
//                        ),
//                    rules =
//                        listOf(
//                            MojangVersionLibraryRule(action = "allow"),
//                            MojangVersionLibraryRule(action = "disallow", os = MojangVersionLibraryRuleOS("OSX")),
//                        ),
//                ),
//            )
//
// //        fun versionWithLibraryWithClassifiersDisallowOsx(): List<MojangVersionLibrary> = TODO()
// //        fun versionWithLibraryAllowOSX(): List<MojangVersionLibrary> = TODO()
// //        fun versionWithLibraryWithClassifiersWithoutArtifactsDisallowOsx(): List<MojangVersionLibrary> = TODO()
// //        fun versionWithLibraryWithClassifiersWithoutArtifactsWithoutRules(): List<MojangVersionLibrary> = TODO()
// //        fun versionWithLibraryWithClassifiersWithoutArtifactsDisallowLinux(): List<MojangVersionLibrary> = TODO()
// //        fun versionWithLibraryWithClassifiersWithoutArtifactsAllowWindows(): List<MojangVersionLibrary> = TODO()
//    }
// }
