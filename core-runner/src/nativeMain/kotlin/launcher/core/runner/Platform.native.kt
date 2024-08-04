package launcher.core.runner

actual fun executeSystemCommand(
    baseDir: String,
    commands: List<String>,
    onExit: () -> Unit,
) {
}
