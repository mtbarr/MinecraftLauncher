package launcher.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import launcher.model.LoginScreenModel
import launcher.screen.theme.DarkTheme
import launcher.state.LauncherState

class LoginScreen : Screen {

  @Composable
  override fun Content() {
    val screenModel = getScreenModel<LoginScreenModel>()
    val state by screenModel.state.collectAsState()
    val launcherState by screenModel.launcherState.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    LaunchedEffect(Unit) {
      screenModel.launcher.preInit()
    }

    DarkTheme {
      Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
      ) {
        Column(
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
          Header(
            enabledSettings = launcherState == LauncherState.Idle,
          ) { navigator.push(OptionsScreen()) }

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(280.dp)
          ) {
            PlayerNameField(
              playerName = state.playerName,
              enabled = launcherState == LauncherState.Idle,
              onNameChange = {
                screenModel.updatePlayerName(it)
                screenModel.scheduleSave()
              }
            )

            Spacer(modifier = Modifier.height(8.dp))

            RememberMeCheckbox(
              checked = state.rememberPlayerName,
              onCheckedChange = { screenModel.updateRememberMe(it) },
              enabled = launcherState == LauncherState.Idle
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlayButton(
              name = when (launcherState) {
                LauncherState.Preparing -> "Preparando..."
                LauncherState.DownloadingJava -> "Baixando Java..."
                LauncherState.Updating -> "Atualizando..."
                LauncherState.Running -> "Executando..."
                else -> "Jogar"
              },
              onClick = { screenModel.launcher.launchGame() },
              blocked = state.playerName.isBlank() || launcherState != LauncherState.Idle
            )
          }

          Footer()
        }
      }
    }
  }

  @Composable
  fun Header(
    enabledSettings: Boolean = true,
    onSettingsClick: () -> Unit,
  ) {
    Box(
      modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
    ) {
      Text(
        text = "DBC BRASIL",
        style = MaterialTheme.typography.h4,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.primary,
        modifier = Modifier.align(Alignment.Center)
      )
      if (enabledSettings) {
        IconButton(
          onClick = onSettingsClick,
          modifier = Modifier.align(Alignment.CenterEnd)
        ) {
          Icon(
            Icons.Default.Settings,
            contentDescription = "Settings",
            tint = MaterialTheme.colors.primary
          )
        }
      }
    }
  }

  @Composable
  fun PlayerNameField(
    playerName: String,
    onNameChange: (String) -> Unit,
    enabled: Boolean = true,
  ) {
    OutlinedTextField(
      value = playerName,
      onValueChange = onNameChange,
      label = { Text("Username") },
      leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
      enabled = enabled
    )
  }

  @Composable
  fun RememberMeCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(text = "Lembrar nome de usuário")
    }
  }

  @Composable
  fun PlayButton(name: String, onClick: () -> Unit, blocked: Boolean) {
    Button(
      onClick = onClick,
      modifier = Modifier.fillMaxWidth().height(48.dp),
      shape = RoundedCornerShape(8.dp),
      enabled = !blocked
    ) {
      Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(24.dp))
      Spacer(Modifier.width(8.dp))
      Text(text = name)
    }
  }

  @Composable
  fun Footer() {
    Text(
      text = "Não associado com a Mojang",
      style = MaterialTheme.typography.caption,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    )
  }
}