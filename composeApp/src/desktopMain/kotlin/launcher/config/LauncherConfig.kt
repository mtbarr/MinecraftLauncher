package launcher.config

import screens.model.UserConfig
import java.io.File

typealias Arch = String

val defaultJavaArguments =
    listOf(
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseG1GC",
        "-XX:G1NewSizePercent=20",
        "-XX:G1ReservePercent=20",
        "-XX:MaxGCPauseMillis=50",
        "-XX:G1HeapRegionSize=32M",
        "-Xms256m",
        "-Xmx256m",
    )

data class LauncherConfig(
    val gameDir: String = "/home/rafael/.minecraftlauncher/game",
    val assetsDir: String = "/home/rafael/.minecraftlauncher/assets",
    val librariesDir: String = "/home/rafael/.minecraftlauncher/libraries",
    val versionsDir: String = "/home/rafael/.minecraftlauncher/versions",
    val nativesDir: String = "/home/rafael/.minecraftlauncher/natives",
    val platform: Platform,
    val arch: Arch,
    val javaPath: String = "java",
    val javaArguments: List<String> = defaultJavaArguments,
    val username: String = "RafaelBacano",
    val assetIndexBaseDir: String = "https://resources.download.minecraft.net",
    val versionFilePath: String = "C:\\Users\\Rafael\\Downloads\\MinecraftLauncher\\server\\1.8.9.json",
) {
    companion object {
        fun fromUserConfig(
            platformData: PlatformData,
            userConfig: UserConfig,
        ): LauncherConfig {
            return LauncherConfig(
                gameDir = userConfig.baseDir + File.separator + "game",
                assetsDir = userConfig.baseDir + File.separator + "assets",
                librariesDir = userConfig.baseDir + File.separator + "libraries",
                versionsDir = userConfig.baseDir + File.separator + "versions",
                nativesDir = userConfig.baseDir + File.separator + "natives",
                platform = platformData.platform,
                arch = platformData.arch,
                javaPath = userConfig.javaPath,
                javaArguments = defaultJavaArguments,
                username = userConfig.username,
            )
        }
    }
}

enum class Platform(val nativeId: String) {
    LINUX("linux"),
    OSX("osx"),
    WINDOWS("windows"),
}

data class PlatformData(
    val platform: Platform,
    val arch: Arch,
    val appDataDir: String,
)
