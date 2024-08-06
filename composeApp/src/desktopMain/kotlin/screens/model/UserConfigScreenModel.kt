package screens.model

import cafe.adriel.voyager.core.model.StateScreenModel
import launcher.config.LauncherConfig

class UserConfigScreenModel(
    private val launcherConfigHolder: LauncherConfigHolder,
    defaultUserConfig: UserConfig,
) : StateScreenModel<UserConfig>(defaultUserConfig) {
    fun updateUsername(username: String) = mutableState.tryEmit(state.value.copy(username = username))

    fun updateJavaPath(javaPath: String) = mutableState.tryEmit(state.value.copy(javaPath = javaPath))

    fun save() {
        launcherConfigHolder.launcherConfig.tryEmit(LauncherConfig.fromUserConfig(state.value))
    }
}

data class UserConfig(
    val username: String,
    val javaPath: String,
)
