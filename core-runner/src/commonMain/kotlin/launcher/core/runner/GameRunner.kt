package launcher.core.runner

import launcher.core.Launcher
import launcher.core.file.ResourceFileType.LIBRARY
import launcher.core.file.ResourceFileType.VERSION
import launcher.core.version.VersionRunnerData

object GameRunner {
    suspend fun launchGame(
        launcher: Launcher,
        javaPath: String,
        javaArguments: List<String>,
        username: String,
        onExit: () -> Unit = {},
    ) {
        launcher.downloadVersionsFile()
        launcher.downloadResources()

        val versionRunnerData =
            VersionRunnerData.create(
                minecraftVersion = launcher.selectedMinecraftVersion!!,
                forgeVersion = launcher.selectedForgeVersion,
            )

        val commands =
            GameRunnerCommandBuilder.buildCommand(
                versionData = versionRunnerData,
                gameFolders = launcher.gameFolders,
                librariesPath =
                    with(launcher.selectedVersion!!) {
                        resourcesWithType(LIBRARY).map { it.location } + resourcesWithType(VERSION).map { it.location }
                    },
                javaPath = javaPath,
                javaArguments = javaArguments,
                username = username,
            )
        commands.forEach(::println)
        executeSystemCommand(baseDir = launcher.gameFolders.gameDir, commands = commands, onExit = onExit)
    }
}
