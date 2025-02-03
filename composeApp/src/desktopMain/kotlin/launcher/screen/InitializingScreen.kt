package launcher.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import launcher.model.DownloadingScreenModel

class InitializingScreen : Screen {

  @Composable
  override fun Content() {
    val screenModel = getScreenModel<DownloadingScreenModel>()
    val state by screenModel.state.collectAsState()

    val navigator = LocalNavigator.currentOrThrow

    ElegantDarkTheme {
      LoadingProgressBar(
        subjectName = state.subjectName,
        progress = state.progress,
        isDownloading = state.isDownloading,
        isError = state.isError,
        isFinished = state.isFinished
      )

      if (state.isFinished) {
        LaunchedEffect(Unit) {
          navigator.push(LoginScreen())
        }
      }
    }
  }

  @Composable
  fun ElegantDarkTheme(content: @Composable () -> Unit) {
    val darkColors = darkColors(
      primary = Color(0xFF6B7280),
      background = Color(0xFF1F2937),
      surface = Color(0xFF374151),
      onBackground = Color.White,
      onSurface = Color.White
    )

    MaterialTheme(
      colors = darkColors,
      typography = Typography(),
      shapes = Shapes(),
      content = content
    )
  }

  @Composable
  fun LoadingProgressBar(
    subjectName: String,
    progress: Int,
    isDownloading: Boolean,
    isError: Boolean,
    isFinished: Boolean,
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
        .padding(16.dp)
    ) {
      Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
      ) {
        Card(
          modifier = Modifier
            .width(300.dp)
            .clip(RoundedCornerShape(16.dp)),
          backgroundColor = MaterialTheme.colors.surface,
          elevation = 8.dp
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
          ) {
            Text(
              text = "Downloading $subjectName",
              style = MaterialTheme.typography.h6,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center,
              color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(
              progress = progress / 100f,
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = MaterialTheme.colors.primary,
              backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = when {
                isDownloading -> "Downloading..."
                isError -> "Error downloading $subjectName"
                isFinished -> "Download finished!"
                else -> ""
              },
              style = MaterialTheme.typography.body1,
              textAlign = TextAlign.Center,
              color = when {
                isError -> Color.Red
                isFinished -> Color.Green
                else -> MaterialTheme.colors.onSurface
              }
            )
          }
        }
      }
    }
  }
}

