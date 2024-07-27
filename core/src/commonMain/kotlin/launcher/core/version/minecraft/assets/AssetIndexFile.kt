package launcher.core.version.minecraft.assets

import kotlinx.serialization.Serializable
import launcher.core.file.GameFolders
import launcher.core.file.ResourceFile
import launcher.core.file.ResourceFileType.ASSET

@Serializable
data class AssetIndexFile(
    val objects: Map<String, AssetIndexFileObject>,
)

@Serializable
data class AssetIndexFileObject(
    val hash: String,
    val size: Long,
) {
    fun toResourceFile(
        gameFolders: GameFolders,
        repositoryUrl: String,
    ): ResourceFile {
        return ResourceFile(
            type = ASSET,
            remoteUrl = buildDownloadUrl(repositoryUrl),
            location = buildLocation(gameFolders.assetsDir),
        )
    }

    private fun buildDownloadUrl(repositoryUrl: String): String {
        val prefix = hash.take(2)
        return "$repositoryUrl/$prefix/$hash"
    }

    private fun buildLocation(assetsDir: String): String {
        val baseDir = "$assetsDir/objects"
        val prefix = hash.take(2)

        return "$baseDir/$prefix/$hash"
    }
}
