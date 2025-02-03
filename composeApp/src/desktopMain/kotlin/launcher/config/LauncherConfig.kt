package launcher.config

import java.nio.file.Paths

data class LauncherConfig(
  val launcherVersion: Int = 1,
  val userName: String = "",
  val javaPath: String = Paths.get(System.getProperty("java.home"), "bin", "java").toString(),
  val maxRam: Int = 1024, // 1GB,
  val rememberUserName: Boolean = false,
)