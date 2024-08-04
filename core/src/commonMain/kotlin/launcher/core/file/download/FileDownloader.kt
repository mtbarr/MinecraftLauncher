package launcher.core.file.download

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.http.Url
import io.ktor.http.isSuccess

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
        val response =
            httpClient.get(Url(url)) {
                onDownload { bytesSentTotal, contentLength ->
                    val percentage = bytesSentTotal / contentLength * 100.0
                    val downloadProgress =
                        DownloadProgress(
                            fileName = url,
                            progress = percentage,
                        )
                    onDownloadProgress(downloadProgress)
                }
            }

        return if (response.status.isSuccess()) {
            response.body()
        } else {
            error("Failed to download file from url [$url] with status [${response.status}]")
        }
    }
}
