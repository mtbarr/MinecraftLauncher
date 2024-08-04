import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.serialization.json.Json
import launcher.core.Launcher
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import screens.HomeScreen
import screens.UserConfigScreen
import screens.model.HomeScreenModel
import screens.model.LauncherConfigHolder
import screens.model.UserConfigScreenModel

@Composable
fun App() {
    MaterialTheme {
        KoinApplication({ modules(applicationModule) }) {
            Navigator(
                listOf(UserConfigScreen(), HomeScreen()),
            )
        }
    }
}

private val applicationModule =
    module {
        single<Json> { Json { ignoreUnknownKeys = true } }
        single<LauncherConfigHolder> { LauncherConfigHolder() }
        single<Launcher> { Launcher.start(get()) }
        single<UserConfigScreenModel> { UserConfigScreenModel(get()) }
        single<HomeScreenModel> { HomeScreenModel(get(), get()) }
    }
