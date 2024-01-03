import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
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

private val applicationModule =
    DI.Module("application") {
        bindSingleton<LauncherConfigHolder> { LauncherConfigHolder() }
        bindSingleton<UserConfigScreenModel> { UserConfigScreenModel(instance()) }
        bindSingleton<HomeScreenModel> { HomeScreenModel(instance()) }
    }

private val di =
    DI {
        import(applicationModule)
    }
