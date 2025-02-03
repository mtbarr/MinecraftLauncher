package launcher.screen.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DarkTheme(content: @Composable () -> Unit) {
  val darkColors = darkColors(
    primary = Color(0xFF64FFDA),
    primaryVariant = Color(0xFF00BFA5),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    error = Color(0xFFCF6679),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
  )

  MaterialTheme(
    colors = darkColors,
    typography = Typography(),
    shapes = Shapes(),
    content = content
  )
}