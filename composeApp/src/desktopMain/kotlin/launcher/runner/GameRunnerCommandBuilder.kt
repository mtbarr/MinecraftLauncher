package launcher.runner

import launcher.config.LauncherConfig
import launcher.version.data.VersionData
import launcher.version.libraries.LibraryPath

object GameRunnerCommandBuilder {
    fun buildCommand(
        launcherConfig: LauncherConfig,
        versionData: VersionData,
        librariesPath: List<LibraryPath>,
    ): List<String> {
        return buildList {
            add(launcherConfig.javaPath)
            addAll(launcherConfig.javaArguments)

            add("-Djava.library.path=${launcherConfig.nativesDir}")

            add("-cp")
            add(librariesPath.joinToString(separator = ";"))
            add(versionData.mainClass)

            addAll(buildMinecraftArguments(launcherConfig, versionData))
            add("--server")
            add("redescreen.com")
            add("--port")
            add("25565")
        }
    }

    private fun buildMinecraftArguments(
        launcherConfig: LauncherConfig,
        versionData: VersionData,
    ): List<String> {
        val replacedArguments =
            versionData.minecraftArguments
                .replace("\${auth_player_name}", launcherConfig.username)
                .replace("\${version_name}", versionData.versionId)
                .replace("\${game_directory}", launcherConfig.gameDir)
                .replace("\${assets_root}", launcherConfig.assetsDir)
                .replace("\${assets_index_name}", versionData.assetIndexId)
                .replace("\${auth_uuid}", "abcde")
                .replace("\${auth_access_token}", "token")
                .replace("\${user_type}", "msa")
                .replace("\${user_properties}", "{}")

        return replacedArguments.split(" ")
    }
}
