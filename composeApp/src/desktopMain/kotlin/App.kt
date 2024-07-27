import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.serialization.json.Json
import launcher.core.Launcher
import launcher.core.config.VersionConfig
import launcher.core.loadJsonFile
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.compose.withDI
import org.kodein.di.instance
import screens.HomeScreen
import screens.UserConfigScreen
import screens.model.HomeScreenModel
import screens.model.LauncherConfigHolder
import screens.model.UserConfigScreenModel

@Composable
fun App() {
    MaterialTheme {
        withDI(di) {
            Navigator(
                listOf(UserConfigScreen(), HomeScreen()),
            )
        }
    }
}

private fun createLauncher(): Launcher {
    val json = Json { ignoreUnknownKeys = true }
    val launcher = Launcher.start(json, launcherFolderName = ".launcher3")

    val versionConfig = loadJsonFile<VersionConfig>(json, "/home/rafael/Projects/MinecraftLauncher/server/versions.json")
    launcher.selectedVersion = versionConfig.versions.first().toVersion(launcher.gameFolders)

    return launcher
}

private val applicationModule =
    DI.Module("application") {
        bindSingleton<LauncherConfigHolder> { LauncherConfigHolder() }
        bindSingleton<Launcher> { createLauncher() }
        bindSingleton<UserConfigScreenModel> { UserConfigScreenModel(instance()) }
        bindSingleton<HomeScreenModel> { HomeScreenModel(instance(), instance()) }
    }

private val di =
    DI {
        import(applicationModule)
    }
