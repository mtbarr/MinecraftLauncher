package launcher.core.file.download

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.http.Url

interface FileDownloader {
    suspend fun download(
        url: String,
        onDownloadProgress: suspend (DownloadProgress) -> Unit,
    ): ByteArray
}

class FileDownloaderAdapter(private val httpClient: HttpClient) : FileDownloader {
    override suspend fun download(
        url: String,
        onDownloadProgress: suspend (DownloadProgress) -> Unit,
    ): ByteArray {
        return httpClient.get(Url(url)) {
            onDownload { bytesSentTotal, contentLength ->
                val percentage = bytesSentTotal / contentLength * 100.0
                val downloadProgress =
                    DownloadProgress(
                        fileName = url,
                        progress = percentage,
                    )
                onDownloadProgress(downloadProgress)
            }
        }.body()
    }
}
