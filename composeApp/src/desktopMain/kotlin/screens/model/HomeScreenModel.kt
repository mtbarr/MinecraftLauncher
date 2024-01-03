package screens.model

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
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
) : StateScreenModel<HomeModel>(HomeModel()) {
    fun startGame() =
        screenModelScope.launch {
            with(state.value) {
                if (isRunning || isLoading) return@launch
            }

            launcherConfigHolder.launcherConfig.value?.let { config ->
                mutableState.emit(state.value.copy(isLoading = true))

                val process = GameRunner.launchGame(config = config, onExit = ::onProcessTerminate)
                mutableState.emit(state.value.copy(isLoading = false, process = process))
            }
        }

    private fun onProcessTerminate() {
        mutableState.tryEmit(state.value.copy(process = null))
    }
}
