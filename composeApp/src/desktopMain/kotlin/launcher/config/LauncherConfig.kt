package launcher.config

typealias Arch = String

data class LauncherConfig(
    val versionFilePath: String = "/home/rafael/IdeaProjects/MinecraftLauncher/server/1.8.9.json",
    val gameDir: String = "/home/rafael/.minecraftlauncher/game",
    val assetsDir: String = "/home/rafael/.minecraftlauncher/assets",
    val librariesDir: String = "/home/rafael/.minecraftlauncher/libraries",
    val versionsDir: String = "/home/rafael/.minecraftlauncher/versions",
    val nativesDir: String = "/home/rafael/.minecraftlauncher/natives",
    val platform: Platform,
    val arch: Arch,
    val javaPath: String = "java",
    val javaArguments: List<String> = listOf(
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseG1GC",
        "-XX:G1NewSizePercent=20",
        "-XX:G1ReservePercent=20",
        "-XX:MaxGCPauseMillis=50",
        "-XX:G1HeapRegionSize=32M",
        "-Xms256m",
        "-Xmx256m"
    ),
    val username: String = "RafaelBacano"
)


enum class Platform(val nativeId: String) {
    LINUX("linux"),
    OSX("osx"),
    WINDOWS("windows")
}
