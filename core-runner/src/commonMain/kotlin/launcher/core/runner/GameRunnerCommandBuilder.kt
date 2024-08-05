package launcher.core.runner

import launcher.core.file.GameFolders
import launcher.core.pathSeparator
import launcher.core.version.VersionRunnerData

object GameRunnerCommandBuilder {
    fun buildCommand(
        versionData: VersionRunnerData,
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
        versionData: VersionRunnerData,
        username: String,
    ): List<String> {
        var replacedArguments =
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

        val serverAddressArguments =
            versionData.serverAddress?.let { address ->
                val (server, port) =
                    if (address.contains(":")) {
                        val splitAddress = address.split(":")
                        splitAddress.first() to splitAddress.last()
                    } else {
                        address to "25565"
                    }
                "--server $server --port $port"
            }
        serverAddressArguments?.let { replacedArguments += " $it" }

        return replacedArguments.split(" ")
    }
}
