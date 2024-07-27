package screens.model

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import launcher.core.Launcher
import launcher.core.file.download.DownloadProgress
import launcher.runner.GameRunner

data class HomeModel(
    val process: Process? = null,
    val isLoading: Boolean = false,
) {
    val isRunning: Boolean
        get() = process != null
}

class HomeScreenModel(
    private val launcherConfigHolder: LauncherConfigHolder,
    private val launcher: Launcher,
) : StateScreenModel<HomeModel>(HomeModel()) {
    val downloadFlow: MutableSharedFlow<DownloadProgress>
        get() = launcher.downloadFlow

    fun startGame() =
        screenModelScope.launch {
            with(state.value) {
                if (isRunning || isLoading) return@launch
            }

            launcherConfigHolder.launcherConfig.value?.let { config ->
                mutableState.emit(state.value.copy(isLoading = true))

                val process = GameRunner.launchGame(config = config, launcher = launcher, onExit = ::onProcessTerminate)
                mutableState.emit(state.value.copy(isLoading = false, process = process))
            }
        }

    private fun onProcessTerminate() {
        mutableState.tryEmit(state.value.copy(process = null))
    }
}
