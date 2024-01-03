package launcher.runner

import launcher.config.LauncherConfig
import launcher.version.libraries.AssetsManager
import launcher.version.libraries.LibrariesManager
import java.io.File
import java.lang.ProcessBuilder.Redirect

object GameRunner {
    suspend fun launchGame(
        config: LauncherConfig,
        onExit: () -> Unit = {},
    ): Process {
        val (versionData, libraries) = LibrariesManager.loadVersion(config)
        AssetsManager.downloadAssets(config, versionData)

        val commands =
            GameRunnerCommandBuilder.buildCommand(
                launcherConfig = config,
                versionData = versionData,
                librariesPath = libraries,
            )

        return ProcessBuilder(commands).let { processBuilder ->
            processBuilder.redirectInput(Redirect.INHERIT)
            processBuilder.redirectOutput(Redirect.INHERIT)
            processBuilder.redirectError(Redirect.INHERIT)
            processBuilder.directory(File(config.gameDir))
            processBuilder.redirectErrorStream(true)

            processBuilder.start()
        }.also { process ->
            process.onExit().whenCompleteAsync { _, _ -> onExit() }
        }
    }
}
