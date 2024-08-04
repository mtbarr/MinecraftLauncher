package launcher.core.runner

expect fun executeSystemCommand(
    baseDir: String,
    commands: List<String>,
    onExit: () -> Unit = {},
)
