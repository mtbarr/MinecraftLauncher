import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension
import java.awt.Toolkit

fun main() = application {
  Window(
    onCloseRequest = ::exitApplication,
    title = "DBC Brasil",
    icon = painterResource("icon.ico"),
    state = rememberWindowState(placement = WindowPlacement.Floating),
    resizable = true,
    undecorated = true,
  ) {
    window.minimumSize = Dimension(1024, 740)

    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowWidth = 1024
    val windowHeight = 740

    val centerX = (screenSize.width - windowWidth) / 2
    val centerY = (screenSize.height - windowHeight) / 2

    LaunchedEffect(Unit) {
      window.setSize(windowWidth, windowHeight)
      window.setLocation(centerX, centerY)
    }

    MaterialTheme {
      Box(modifier = Modifier.fillMaxSize()) {
        App()
        Row(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(onClick = {
            window.isMinimized = true
          }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minimizar", tint = Color.White)
          }
          IconButton(onClick = ::exitApplication) {
            Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.White)
          }
        }
      }
    }
  }
}

@Composable
@Preview
fun AppDesktopPreview() {
  App()
}
