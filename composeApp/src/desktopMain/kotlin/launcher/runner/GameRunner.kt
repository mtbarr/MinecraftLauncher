package launcher.runner

import launcher.config.LauncherConfig
import launcher.core.Launcher
import launcher.core.file.ResourceFileType.LIBRARY
import launcher.core.file.ResourceFileType.VERSION
import java.io.File
import java.lang.ProcessBuilder.Redirect

object GameRunner {
    suspend fun launchGame(
        config: LauncherConfig,
        launcher: Launcher,
        onExit: () -> Unit = {},
    ): Process {
        launcher.downloadResources()

        val commands =
            GameRunnerCommandBuilder.buildCommand(
                launcherConfig = config,
                versionData = launcher.selectedMinecraftVersion!!,
                gameFolders = launcher.gameFolders,
                librariesPath =
                    with(launcher.selectedVersion!!) {
                        resourcesWithType(LIBRARY).map { it.location } + resourceWithType(VERSION)!!.location
                    },
            )

        return ProcessBuilder(commands).let { processBuilder ->
            processBuilder.redirectInput(Redirect.INHERIT)
            processBuilder.redirectOutput(Redirect.INHERIT)
            processBuilder.redirectError(Redirect.INHERIT)
            processBuilder.directory(File(launcher.gameFolders.gameDir))
            processBuilder.redirectErrorStream(true)

            processBuilder.start()
        }.also { process ->
            process.onExit().whenCompleteAsync { _, _ -> onExit() }
        }
    }
}
