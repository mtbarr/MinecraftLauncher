package launcher.version.json

import kotlinx.serialization.json.Json
import java.io.File

object MojangVersionLoader {
    fun load(
        json: Json,
        path: String,
    ): MojangVersion {
        val file = File(path).readText()
        return json.decodeFromString<MojangVersion>(file)
    }
}
