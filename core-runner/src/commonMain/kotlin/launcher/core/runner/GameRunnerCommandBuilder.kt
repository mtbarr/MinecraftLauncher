package launcher.core.runner

import launcher.core.file.GameFolders
import launcher.core.pathSeparator
import launcher.core.version.minecraft.MappedMinecraftVersion

object GameRunnerCommandBuilder {
    fun buildCommand(
        versionData: MappedMinecraftVersion,
        gameFolders: GameFolders,
        librariesPath: List<String>,
        javaPath: String,
        javaArguments: List<String>,
        username: String,
    ): List<String> {
        return buildList {
            add(javaPath)
            addAll(javaArguments)

            add("-Djava.library.path=${gameFolders.nativesDir}")

            add("-cp")
            add(librariesPath.joinToString(separator = pathSeparator()))
            add(versionData.mainClass)

            addAll(buildMinecraftArguments(gameFolders, versionData, username))
        }
    }

    private fun buildMinecraftArguments(
        gameFolders: GameFolders,
        versionData: MappedMinecraftVersion,
        username: String,
    ): List<String> {
        val replacedArguments =
            versionData.minecraftArguments
                .replace("\${auth_player_name}", username)
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
