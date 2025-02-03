package launcher.model

import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import launcher.Launcher
import launcher.state.DownloadingState

class DownloadingScreenModel(
  val launcher: Launcher,
) : StateScreenModel<DownloadingState>(DownloadingState()) {

  init {
    CoroutineScope(Dispatchers.IO).launch {
      launcher.currentDownloadingState.collect { state ->
        mutableState.tryEmit(state)
      }
    }
  }

  fun updateSubjectName(subjectName: String) {
    mutableState.tryEmit(state.value.copy(subjectName = subjectName))
  }

  fun updateProgress(progress: Int) {
    mutableState.tryEmit(state.value.copy(progress = progress))
  }

  fun updateIsDownloading(isDownloading: Boolean) {
    mutableState.tryEmit(state.value.copy(isDownloading = isDownloading))
  }

  fun updateIsError(isError: Boolean) {
    mutableState.tryEmit(state.value.copy(isError = isError))
  }

  fun updateIsFinished(isFinished: Boolean) {
    mutableState.tryEmit(state.value.copy(isFinished = isFinished))
  }

  fun isFinished(): Boolean {
    return state.value.isFinished
  }
}