package launcher.model

import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import launcher.Launcher
import launcher.state.OptionsScreenState

class OptionsScreenModel(val launcher: Launcher) : StateScreenModel<OptionsScreenState>(OptionsScreenState()) {

  init {
    CoroutineScope(Dispatchers.IO).launch {
      launcher.currentLauncherConfig.collect { config ->
        mutableState.tryEmit(
          state.value.copy(
            jvmPath = config.javaPath,
            memory = config.maxRam,
          )
        )
      }
    }
  }

  fun updateJvmPath(jvmPath: String) {
    mutableState.tryEmit(state.value.copy(jvmPath = jvmPath))
  }

  fun updateMemory(memory: Int) {
    mutableState.tryEmit(state.value.copy(memory = memory))
  }

  fun saveOptions() {
    launcher.currentLauncherConfig.tryEmit(
      launcher.launcherConfig.copy(
        javaPath = state.value.jvmPath,
        maxRam = state.value.memory,
      )
    )

    launcher.saveConfig()
  }
}