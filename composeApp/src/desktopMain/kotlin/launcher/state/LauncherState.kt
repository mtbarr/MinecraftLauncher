package launcher.state

sealed class LauncherState {
  data object Idle : LauncherState()
  data object DownloadingJava : LauncherState()
  data object Preparing : LauncherState()
  data object Updating : LauncherState()
  data object Running : LauncherState()
  data class Error(val message: String) : LauncherState()
}