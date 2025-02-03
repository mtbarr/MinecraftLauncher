package launcher.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import launcher.model.OptionsScreenModel
import launcher.screen.theme.DarkTheme

typealias OnClick = () -> Unit

class OptionsScreen : Screen {

  @Composable
  override fun Content() {
    val screenModel = getScreenModel<OptionsScreenModel>()
    val state by screenModel.state.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    DarkTheme {
      Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
      ) {
        Column(
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(280.dp)
          ) {
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
              value = state.jvmPath,
              onValueChange = { screenModel.updateJvmPath(it) },
              label = { Text("Caminho para o java") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            MemorySelector(
              selectedMemory = state.memory,
              onMemorySelected = { screenModel.updateMemory(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SaveButton(
              onClick = {
                screenModel.saveOptions()
                navigator.push(LoginScreen())
              },
              blocked = state.jvmPath.isEmpty() || state.memory == 0
            )
          }
        }
      }
    }
  }

  @Composable
  fun MemorySelector(
    selectedMemory: Int,
    onMemorySelected: (Int) -> Unit,
  ) {
    val maxMemory = 8192;
    var sliderPosition by remember { mutableStateOf(selectedMemory.toFloat()) }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          "${(sliderPosition / 1024).toInt()}GB",
          color = MaterialTheme.colors.onSurface
        )
        Text(
          "${maxMemory / 1024}GB",
          color = MaterialTheme.colors.onSurface
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Slider(
        value = sliderPosition,
        onValueChange = {
          sliderPosition = it
          onMemorySelected(it.toInt())
        },
        valueRange = 1024f..maxMemory.toFloat(),
        steps = 6,
        colors = SliderDefaults.colors(
          thumbColor = Color(0xFF2ECC71),
          activeTrackColor = Color(0xFF2ECC71),
          inactiveTrackColor = Color(0xFF2F3640)
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF2F3640),
        shape = RoundedCornerShape(8.dp)
      ) {
        Row(
          modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              "${(sliderPosition / 1024).toInt()}GB",
              color = MaterialTheme.colors.onSurface
            )
            Text(
              "${((maxMemory - sliderPosition) / 1024).toInt()}GB Restantes",
              color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
              style = MaterialTheme.typography.caption
            )
          }
        }
      }
    }
  }

  @Composable
  fun SaveButton(
    onClick: OnClick = {},
    blocked: Boolean = false,
  ) {
    Button(
      onClick = onClick,
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      shape = RoundedCornerShape(8.dp),
      colors = ButtonDefaults.buttonColors(
        backgroundColor = if (blocked) MaterialTheme.colors.onSurface.copy(alpha = 0.12f) else MaterialTheme.colors.primary,
        disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
      ),
      enabled = !blocked
    ) {
      Text(
        "Salvar",
        style = MaterialTheme.typography.button,
        color = if (blocked) MaterialTheme.colors.onSurface.copy(alpha = 0.38f) else LocalContentColor.current
      )
    }
  }
}

