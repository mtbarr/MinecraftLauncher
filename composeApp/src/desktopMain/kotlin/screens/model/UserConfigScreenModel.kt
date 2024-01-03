package screens.model

import cafe.adriel.voyager.core.model.StateScreenModel
import getPlatformData
import launcher.config.LauncherConfig
import java.io.File

class UserConfigScreenModel(
    private val launcherConfigHolder: LauncherConfigHolder,
) : StateScreenModel<UserConfig>(UserConfig()) {
    fun updateBaseDir(baseDir: String) = mutableState.tryEmit(state.value.copy(baseDir = baseDir))

    fun updateUsername(username: String) = mutableState.tryEmit(state.value.copy(username = username))

    fun updateJavaPath(javaPath: String) = mutableState.tryEmit(state.value.copy(javaPath = javaPath))

    fun save() {
        launcherConfigHolder.launcherConfig.tryEmit(LauncherConfig.fromUserConfig(getPlatformData(), state.value))
    }
}

data class UserConfig(
    val baseDir: String = getPlatformData().appDataDir + File.separator + ".launcher",
    val username: String = "",
    val javaPath: String = "java",
)
