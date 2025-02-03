import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import launcher.Launcher
import launcher.model.DownloadingScreenModel
import launcher.model.LoginScreenModel
import launcher.model.OptionsScreenModel
import launcher.screen.LoginScreen

import org.koin.compose.KoinApplication
import org.koin.dsl.module


@Composable
fun App() {
  MaterialTheme {
    KoinApplication({ modules(applicationModule) }) {
      Navigator(
        listOf(LoginScreen()),
      )
    }
  }
}

private val applicationModule = module {
  single<Launcher> { Launcher() }
  single<LoginScreenModel> { LoginScreenModel(get()) }
  single<OptionsScreenModel> { OptionsScreenModel(get()) }
  single<DownloadingScreenModel> { DownloadingScreenModel(get()) }
}
