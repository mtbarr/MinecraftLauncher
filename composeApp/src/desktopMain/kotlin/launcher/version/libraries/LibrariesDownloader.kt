package launcher.version.libraries

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.util.toByteArray

object LibrariesDownloader {
    private val client = HttpClient()

    suspend fun download(url: String): ByteArray {
        return client.get(Url(url)).bodyAsChannel().toByteArray()
    }
}
