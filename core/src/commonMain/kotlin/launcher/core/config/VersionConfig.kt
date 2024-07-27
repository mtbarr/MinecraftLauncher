package launcher.core.config

import kotlinx.serialization.Serializable
import launcher.core.config.VersionConfigResourceType.SERVER
import launcher.core.config.VersionConfigResourceType.VERSION
import launcher.core.extensions.withSeparator
import launcher.core.file.GameFolders
import launcher.core.file.ResourceFile
import launcher.core.file.ResourceFileType
import launcher.core.version.Version

@Serializable
data class VersionConfig(
    val versions: List<VersionConfigEntry>,
)

@Serializable
data class VersionConfigEntry(
    val id: String,
    val version: Int,
    val resources: List<VersionConfigResource>,
) {
    fun toVersion(gameFolders: GameFolders): Version {
        return Version(
            id = id,
            version = version,
            resources =
                resources.map { resource ->
                    val resourceFileType = resource.type.toResourceFileType()
                    val baseFolder = resourceFileType.getBaseFolder(gameFolders)

                    ResourceFile(
                        type = resourceFileType,
                        remoteUrl = resource.url,
                        location = resource.buildLocation(id = id, baseFolder = baseFolder),
                    )
                }.toMutableList(),
        )
    }
}

@Serializable
data class VersionConfigResource(
    val type: VersionConfigResourceType,
    val url: String,
) {
    fun buildLocation(
        id: String,
        baseFolder: String,
    ): String {
        return when (type) {
            SERVER -> baseFolder
            VERSION -> baseFolder withSeparator id withSeparator "$id.json"
        }
    }
}

enum class VersionConfigResourceType {
    VERSION,
    SERVER,
    ;

    fun toResourceFileType(): ResourceFileType {
        return when (this) {
            VERSION -> ResourceFileType.VERSION_INFO
            SERVER -> ResourceFileType.SERVER_DATA
        }
    }
}
