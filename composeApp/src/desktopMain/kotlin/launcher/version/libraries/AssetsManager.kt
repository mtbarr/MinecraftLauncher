package launcher.version.libraries

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import launcher.config.LauncherConfig
import launcher.util.downloadFile
import launcher.util.fileExists
import launcher.util.saveIntoFile
import launcher.version.data.VersionData
import java.io.File

object AssetsManager {
    suspend fun downloadAssets(
        config: LauncherConfig,
        versionData: VersionData,
    ) {
        val assetIndexFile = downloadAssetIndexFile(config, versionData)

        assetIndexFile.objects.forEach { (_, assetObject) ->
            val hash = assetObject.hash
            val path = buildAssetPath(config, hash)

            if (!fileExists(path)) {
                val response = downloadFile(buildAssetUrl(config, hash))
                saveIntoFile(path, response)
            }
        }
    }

    private suspend fun downloadAssetIndexFile(
        config: LauncherConfig,
        versionData: VersionData,
    ): AssetIndexFile {
        val path = "${config.assetsDir}/indexes/${versionData.assetIndexId}.json"

        val byteArray =
            if (fileExists(path)) {
                File(path).readBytes()
            } else {
                downloadFile(versionData.assetIndexUrl)
                    .also { saveIntoFile(path, it) }
            }

        return byteArray
            .decodeToString()
            .let { assetIndexJson -> Json.decodeFromString(assetIndexJson) }
    }

    private fun buildAssetUrl(
        config: LauncherConfig,
        hash: String,
    ): String {
        val baseDir = config.assetIndexBaseDir
        val prefix = hash.take(2)

        return "$baseDir/$prefix/$hash"
    }

    private fun buildAssetPath(
        config: LauncherConfig,
        hash: String,
    ): String {
        val baseDir = "${config.assetsDir}/objects"
        val prefix = hash.take(2)

        return "$baseDir/$prefix/$hash"
    }
}

@Serializable
data class AssetIndexFile(
    val objects: Map<String, AssetIndexFileObject>,
)

@Serializable
data class AssetIndexFileObject(
    val hash: String,
    val size: Long,
)
