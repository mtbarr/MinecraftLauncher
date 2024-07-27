package launcher.core.version

import launcher.core.extensions.withSeparator
import launcher.core.extractUrlPath
import launcher.core.file.GameFolders
import launcher.core.file.ResourceFile
import launcher.core.file.ResourceFileType
import launcher.core.file.ResourceFileType.ASSET_INDEX
import launcher.core.file.ResourceFileType.LIBRARY
import launcher.core.file.ResourceFileType.NATIVE
import launcher.core.file.ResourceFileType.VERSION
import launcher.core.file.ResourceFileType.VERSION_INFO
import launcher.core.version.minecraft.MappedMinecraftVersion

data class Version(
    val id: String,
    val version: Int,
    val resources: MutableList<ResourceFile>,
) {
    val versionInfoResource: ResourceFile?
        get() = resourceWithType(VERSION_INFO)

    val assetIndexResource: ResourceFile?
        get() = resourceWithType(ASSET_INDEX)

    fun resourceWithType(type: ResourceFileType): ResourceFile? {
        return resources.firstOrNull { it.type == type }
    }

    fun resourcesWithType(type: ResourceFileType): List<ResourceFile> {
        return resources.filter { it.type == type }
    }

    fun loadMinecraftVersionResources(
        minecraftVersion: MappedMinecraftVersion,
        gameFolders: GameFolders,
    ) {
        val versionClientResource =
            ResourceFile(
                type = VERSION,
                remoteUrl = minecraftVersion.versionLibrary.url,
                location = VERSION.getBaseFolder(gameFolders) withSeparator id withSeparator "$id.jar",
            )

        val (nativeLibraries, libraries) = minecraftVersion.libraries.partition { it.isNative }

        val libraryBaseFolder = LIBRARY.getBaseFolder(gameFolders)
        val librariesResources =
            libraries.map { library ->
                val urlSuffix = extractUrlPath(library.url)
                ResourceFile(
                    type = LIBRARY,
                    remoteUrl = library.url,
                    location = libraryBaseFolder withSeparator urlSuffix,
                )
            }

        val nativeCacheBaseFolders = NATIVE.getBaseFolder(gameFolders)
        val nativeLibrariesResources =
            nativeLibraries.map { library ->
                val urlSuffix = extractUrlPath(library.url)
                ResourceFile(
                    type = NATIVE,
                    remoteUrl = library.url,
                    location = nativeCacheBaseFolders withSeparator urlSuffix,
                )
            }

        val assetIndexBaseFolder = ASSET_INDEX.getBaseFolder(gameFolders)
        val assetIndexResource =
            with(minecraftVersion) {
                ResourceFile(
                    type = ASSET_INDEX,
                    remoteUrl = assetIndexUrl,
                    location = assetIndexBaseFolder withSeparator "indexes" withSeparator "$assetIndexId.json",
                )
            }

        resources.add(versionClientResource)
        resources.addAll(librariesResources)
        resources.addAll(nativeLibrariesResources)
        resources.add(assetIndexResource)
    }
}
