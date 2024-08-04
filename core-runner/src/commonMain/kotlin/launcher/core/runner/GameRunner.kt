package launcher.core.runner

import launcher.core.Launcher
import launcher.core.file.ResourceFileType.LIBRARY
import launcher.core.file.ResourceFileType.VERSION

object GameRunner {
    suspend fun launchGame(
        launcher: Launcher,
        javaPath: String,
        javaArguments: List<String>,
        username: String,
        onExit: () -> Unit = {},
    ) {
        launcher.downloadResources()

        val commands =
            GameRunnerCommandBuilder.buildCommand(
                versionData = launcher.selectedMinecraftVersion!!,
                gameFolders = launcher.gameFolders,
                librariesPath =
                    with(launcher.selectedVersion!!) {
                        resourcesWithType(LIBRARY).map { it.location } + resourceWithType(VERSION)!!.location
                    },
                javaPath = javaPath,
                javaArguments = javaArguments,
                username = username,
            )

        executeSystemCommand(baseDir = launcher.gameFolders.gameDir, commands = commands, onExit = onExit)
    }
}
