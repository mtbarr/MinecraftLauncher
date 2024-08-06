package screens.model

import cafe.adriel.voyager.core.model.StateScreenModel
import launcher.config.LauncherConfig

class UserConfigScreenModel(
    private val launcherConfigHolder: LauncherConfigHolder,
) : StateScreenModel<UserConfig>(UserConfig()) {
    fun updateUsername(username: String) = mutableState.tryEmit(state.value.copy(username = username))

    fun updateJavaPath(javaPath: String) = mutableState.tryEmit(state.value.copy(javaPath = javaPath))

    fun save() {
        launcherConfigHolder.launcherConfig.tryEmit(LauncherConfig.fromUserConfig(state.value))
    }
}

data class UserConfig(
    val username: String = "RafaelBacano",
    val javaPath: String = "/home/rafael/.sdkman/candidates/java/8.0.412-amzn/bin/java",
)
