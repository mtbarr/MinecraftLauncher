package screens.model

import kotlinx.coroutines.flow.MutableStateFlow
import launcher.config.LauncherConfig

data class LauncherConfigHolder(
    var launcherConfig: MutableStateFlow<LauncherConfig?> = MutableStateFlow(null),
)
