package launcher.runner

import launcher.config.LauncherConfig
import launcher.core.file.GameFolders
import launcher.core.pathSeparator
import launcher.core.version.minecraft.MappedMinecraftVersion

object GameRunnerCommandBuilder {
    fun buildCommand(
        launcherConfig: LauncherConfig,
        versionData: MappedMinecraftVersion,
        gameFolders: GameFolders,
        librariesPath: List<String>,
    ): List<String> {
        return buildList {
            add(launcherConfig.javaPath)
            addAll(launcherConfig.javaArguments)

            add("-Djava.library.path=${gameFolders.nativesDir}")

            add("-cp")
            add(librariesPath.joinToString(separator = pathSeparator()))
            add(versionData.mainClass)

            addAll(buildMinecraftArguments(launcherConfig, gameFolders, versionData))
        }
    }

    private fun buildMinecraftArguments(
        launcherConfig: LauncherConfig,
        gameFolders: GameFolders,
        versionData: MappedMinecraftVersion,
    ): List<String> {
        val replacedArguments =
            versionData.minecraftArguments
                .replace("\${auth_player_name}", launcherConfig.username)
                .replace("\${version_name}", versionData.versionId)
                .replace("\${game_directory}", gameFolders.gameDir)
                .replace("\${assets_root}", gameFolders.assetsDir)
                .replace("\${assets_index_name}", versionData.assetIndexId)
                .replace("\${auth_uuid}", "abcde")
                .replace("\${auth_access_token}", "token")
                .replace("\${user_type}", "msa")
                .replace("\${user_properties}", "{}")

        return replacedArguments.split(" ")
    }
}
