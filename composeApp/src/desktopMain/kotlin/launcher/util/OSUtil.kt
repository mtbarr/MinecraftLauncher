package launcher.util

enum class OperatingSystem {
  WINDOWS, MAC, LINUX, UNKNOWN;

  companion object {
    fun detect(): OperatingSystem {
      val osName = System.getProperty("os.name").lowercase()
      return when {
        osName.contains("win") -> WINDOWS
        osName.contains("mac") -> MAC
        osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> LINUX
        else -> UNKNOWN
      }
    }
  }
}

object OSUtil {
  val currentOS: OperatingSystem by lazy { OperatingSystem.detect() }
}
