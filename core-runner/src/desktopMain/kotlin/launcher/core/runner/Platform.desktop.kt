package launcher.core.runner

import java.io.File
import java.lang.ProcessBuilder.Redirect

actual fun executeSystemCommand(
    baseDir: String,
    commands: List<String>,
    onExit: () -> Unit,
) {
    ProcessBuilder(commands).let { processBuilder ->
        processBuilder.redirectInput(Redirect.INHERIT)
        processBuilder.redirectOutput(Redirect.INHERIT)
        processBuilder.redirectError(Redirect.INHERIT)
        processBuilder.directory(File(baseDir))
        processBuilder.redirectErrorStream(true)

        processBuilder.start()
    }.also { process ->
        process.onExit().whenCompleteAsync { _, _ -> onExit() }
    }
}
