package launcher.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import launcher.core.config.VersionConfig

fun main() =
    runBlocking {
        val json = Json { ignoreUnknownKeys = true }
        val launcher = Launcher.start(json)

        CoroutineScope(Dispatchers.IO).launch {
            launcher.downloadFlow.collect {
                println("PROGRESS: $it")
            }
        }

        val versionConfig = loadJsonFile<VersionConfig>(json, "/home/rafael/Projects/MinecraftLauncher/server/versions.json")

        launcher.selectedVersion = versionConfig.versions.first().toVersion(launcher.gameFolders)
        launcher.downloadResources()
    }
