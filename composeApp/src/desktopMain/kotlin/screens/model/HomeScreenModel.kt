package screens.model

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import launcher.core.Launcher
import launcher.core.file.download.DownloadProgress
import launcher.core.runner.GameRunner

data class HomeModel(
    val isRunning: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

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
                mutableState.emit(state.value.copy(isLoading = true, error = null))

                runCatching {
                    GameRunner.launchGame(
                        launcher = launcher,
                        javaPath = config.javaPath,
                        javaArguments = config.javaArguments,
                        username = config.username,
                        onExit = ::onProcessTerminate,
                    )
                }.onFailure { throwable ->
                    mutableState.emit(
                        state.value.copy(
                            isLoading = false,
                            isRunning = false,
                            error = throwable.stackTraceToString(),
                        ),
                    )
                }.onSuccess {
                    mutableState.emit(state.value.copy(isLoading = false, isRunning = true, error = null))
                }
            }
        }

    fun dismissError() =
        screenModelScope.launch {
            mutableState.tryEmit(state.value.copy(error = null))
            launcher.clearDownloadFlow()
        }

    private fun onProcessTerminate() {
        mutableState.tryEmit(state.value.copy(isRunning = false))
    }
}
