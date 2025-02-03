package launcher.model

import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import launcher.Launcher
import launcher.state.LoginState

class LoginScreenModel(
  val launcher: Launcher,
) : StateScreenModel<LoginState>(LoginState("", false)) {

  private val saveJob = MutableStateFlow<Job?>(null)

  val launcherState = launcher.launcherState

  init {
    // Atualiza o estado quando a configuração do launcher mudar
    CoroutineScope(Dispatchers.IO).launch {
      launcher.currentLauncherConfig.collect { config ->
        mutableState.tryEmit(LoginState(config.userName, config.rememberUserName))
      }
    }
  }

  fun updatePlayerName(playerName: String) {
    mutableState.tryEmit(state.value.copy(playerName = playerName))
    scheduleSave()
  }

  fun updateRememberMe(rememberMe: Boolean) {
    mutableState.tryEmit(state.value.copy(rememberPlayerName = rememberMe))
    scheduleSave()
  }

  fun scheduleSave() {
    saveJob.value?.cancel()
    saveJob.value = CoroutineScope(Dispatchers.IO).launch {
      delay(250L)
      launcher.currentLauncherConfig.tryEmit(
        launcher.launcherConfig.copy(
          userName = state.value.playerName,
          rememberUserName = state.value.rememberPlayerName
        )
      )
      launcher.saveConfig()
    }
  }
}