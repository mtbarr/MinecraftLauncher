package launcher.runner

import launcher.config.LauncherConfig
import launcher.config.Platform.LINUX
import launcher.version.libraries.LibrariesManager
import java.io.File
import java.lang.ProcessBuilder.Redirect
import java.util.UUID

object GameRunner {

    suspend fun launchGame(config: LauncherConfig): Process {
        val (versionData, libraries) = LibrariesManager.loadVersion(config)

        val commands = GameRunnerCommandBuilder.buildCommand(
            launcherConfig = config,
            versionData = versionData,
            librariesPath = libraries
        )

        return ProcessBuilder(commands).let { processBuilder ->
            processBuilder.redirectInput(Redirect.INHERIT)
            processBuilder.redirectOutput(Redirect.INHERIT)
            processBuilder.redirectError(Redirect.INHERIT)
            processBuilder.directory(File(config.gameDir))
            processBuilder.redirectErrorStream(true)

            processBuilder.start()
        }
    }



    suspend fun openLauncher(): Process {
        val path = "/home/rafael/IdeaProjects/MinecraftLauncher/server/1.8.9.json"
        val config = LauncherConfig(
            platform = LINUX,
            arch = "64",
            javaPath = "/home/rafael/.sdkman/candidates/java/8.0.372-amzn/bin/java",
            username = "BadGay${UUID.randomUUID().toString().take(10)}"
        )
    }
}