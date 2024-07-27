package launcher.config

import screens.model.UserConfig

val defaultJavaArguments =
    listOf(
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseG1GC",
        "-XX:G1NewSizePercent=20",
        "-XX:G1ReservePercent=20",
        "-XX:MaxGCPauseMillis=50",
        "-XX:G1HeapRegionSize=32M",
        "-XX:-OptimizeStringConcat",
        "-Xms1G",
        "-Xmx1G",
    )

data class LauncherConfig(
    val javaPath: String,
    val javaArguments: List<String> = defaultJavaArguments,
    val username: String = "RafaelBacano",
) {
    companion object {
        fun fromUserConfig(userConfig: UserConfig): LauncherConfig {
            return LauncherConfig(
                javaPath = userConfig.javaPath,
                javaArguments = defaultJavaArguments,
                username = userConfig.username,
            )
        }
    }
}
