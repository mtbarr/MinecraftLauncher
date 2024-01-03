package launcher.util

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.flow.MutableSharedFlow
import java.math.BigDecimal
import java.math.MathContext

object FileDownloader {
    private val client = HttpClient()

    val downloadProgress = MutableSharedFlow<DownloadProgress>()

    suspend fun download(url: String): ByteArray {
        return client.get(Url(url)) {
            onDownload { bytesSentTotal, contentLength ->
                val bytesSentTotalBigDecimal = BigDecimal(bytesSentTotal)
                val contentLengthBigDecimal = BigDecimal(contentLength)
                val percentage =
                    (bytesSentTotalBigDecimal.divide(contentLengthBigDecimal, MathContext.DECIMAL64))
                println("Percentage: $percentage Float: ${percentage.toFloat()}")
                downloadProgress.emit(
                    DownloadProgress(
                        fileName = this.url.toString(),
                        progress = percentage,
                    ),
                )
            }
        }.bodyAsChannel().toByteArray()
    }
}

suspend fun downloadFile(url: String): ByteArray = FileDownloader.download(url)

data class DownloadProgress(
    val fileName: String,
    val progress: BigDecimal,
)
