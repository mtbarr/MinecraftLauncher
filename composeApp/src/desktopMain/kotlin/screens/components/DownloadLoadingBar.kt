package screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import launcher.util.FileDownloader
import java.math.BigDecimal
import kotlin.time.Duration.Companion.seconds

private val MAX = BigDecimal("1")

@Composable
fun DownloadLoadingBar() {
    val nullableProgress by FileDownloader.downloadProgress.collectAsState(null)
    var lastProgressToClean by remember {
        val lastProgress: String? = null
        mutableStateOf(lastProgress to false)
    }

    val progress = nullableProgress

    LaunchedEffect(lastProgressToClean) {
        progress?.let {
            if (it.progress == MAX) {
                delay(5.seconds)
                if (this.isActive) {
                    lastProgressToClean = (progress.fileName to true)
                }
            }
        }
    }

    println(progress.toString())

    val shouldClean =
        with(lastProgressToClean) {
            first != null && first == nullableProgress?.fileName && second
        }

    if (progress != null && !shouldClean) {
        Column {
            Text("Baixando arquivo: ${progress.fileName}")
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress.progress.toFloat(),
                modifier = Modifier.fillMaxWidth(0.7f),
            )
        }
    }
}
