import launcher.config.Platform
import launcher.config.PlatformData
import launcher.util.fileExists
import java.io.File

fun getPlatformData(): PlatformData {
    val osName = System.getProperty("os.name", "generic").lowercase()
    val platform =
        when {
            osName.contains("mac") || osName.contains("darwin") -> Platform.OSX
            osName.contains("win") -> Platform.WINDOWS
            else -> Platform.LINUX
        }
    val arch =
        System.getProperty("os.arch", "x86").let { arch ->
            when {
                arch.contains("64") -> "64"
                arch.contains("32") || arch.contains("86") -> "32"
                else -> arch
            }
        }

    return PlatformData(platform = platform, arch = arch, appDataDir = getAppDataDir())
}

private fun getAppDataDir(): String {
    val home = System.getProperty("user.home")
    val separator = File.separator

    val appDataFolder = "$home${separator}AppData${separator}Roaming"

    return if (fileExists(appDataFolder)) {
        appDataFolder
    } else {
        home
    }
}
