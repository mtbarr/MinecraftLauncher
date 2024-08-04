package launcher.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

fun main() =
    runBlocking {
        val json = Json { ignoreUnknownKeys = true }
        val launcher = Launcher.start(json)

        CoroutineScope(Dispatchers.IO).launch {
            launcher.downloadFlow.collect {
                println("PROGRESS: $it")
            }
        }

        launcher.downloadVersionsFile()
        launcher.downloadResources()
    }
