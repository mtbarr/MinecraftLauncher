package screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import screens.components.DownloadLoadingBar
import screens.model.HomeScreenModel
import screens.model.LauncherConfigHolder

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel<HomeScreenModel>()
        val state by screenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        val launcherConfigHolder by localDI().instance<LauncherConfigHolder>()
        val isLaunchButtonEnabled = !state.isRunning && !state.isLoading && launcherConfigHolder.launcherConfig.value != null
        ContentState(
            isLaunchButtonEnabled = isLaunchButtonEnabled,
            onClickOptions = { navigator.push(UserConfigScreen()) },
            onLaunchButtonClick = { screenModel.startGame() },
        )
    }

    @Composable
    fun ContentState(
        isLaunchButtonEnabled: Boolean = true,
        onLaunchButtonClick: () -> Unit = {},
        onClickOptions: () -> Unit = {},
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Row {
                OptionsButton(onClickOptions)
                LaunchButton(isLaunchButtonEnabled, onLaunchButtonClick)
            }
            Spacer(modifier = Modifier.height(32.dp))
            DownloadLoadingBar()
        }
    }

    @Composable
    fun OptionsButton(onClickOptions: () -> Unit) {
        TextButton(onClick = onClickOptions) {
            Text("Opções")
        }
    }

    @Composable
    fun LaunchButton(
        isEnabled: Boolean = true,
        onClick: () -> Unit,
    ) {
        TextButton(enabled = isEnabled, onClick = onClick) {
            Text("Jogar")
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen().ContentState()
}
