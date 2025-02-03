package launcher.state

data class DownloadingState(
  val subjectName: String = "",
  val progress: Int = 0,
  val isDownloading: Boolean = false,
  val isError: Boolean = false,
  val isFinished: Boolean = false,
)

