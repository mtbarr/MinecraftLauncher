package launcher.core

import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformUtil {
    @Test
    fun testExtractUrlPath() {
        val url = "https://example.com/path/to/file.txt"
        val expected = "/path/to/file.txt"
        val actual = extractUrlPath(url)
        assertEquals(expected = expected, actual = actual)
    }
}
