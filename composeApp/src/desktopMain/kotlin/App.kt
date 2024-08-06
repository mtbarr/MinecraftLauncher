import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.serialization.json.Json
import launcher.core.Launcher
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import screens.HomeScreen
import screens.UserConfigScreen
import screens.model.HomeScreenModel
import screens.model.LauncherConfigHolder
import screens.model.UserConfig
import screens.model.UserConfigScreenModel
import java.io.File

@Composable
fun App() {
    MaterialTheme {
        KoinApplication({ modules(applicationModule) }) {
            Navigator(
                listOf(UserConfigScreen(), HomeScreen()),
            )
        }
    }
}

private val applicationModule =
    module {
        single<Json> { Json { ignoreUnknownKeys = true } }
        single<LauncherConfigHolder> { LauncherConfigHolder() }
        single<Launcher> { Launcher.start(get()) }
        single<UserConfigScreenModel> { UserConfigScreenModel(get(), defaultUserConfig()) }
        single<HomeScreenModel> { HomeScreenModel(get(), get()) }
    }

private fun defaultUserConfig(): UserConfig {
    val javaVersions = getJavaExecutables()
    javaVersions.forEach(::println)

    return UserConfig(
        username = System.getenv("USER") ?: "",
        javaPath = javaVersions.firstOrNull().orEmpty(),
    )
}

fun getJavaExecutables(): List<String> {
    val osName = System.getProperty("os.name").lowercase()
    val javaExecutables = mutableListOf<String>()

    when {
        osName.contains("win") -> {
            val windowsPaths =
                listOf(
                    "C:\\Program Files\\Java",
                    "C:\\Program Files (x86)\\Java",
                )
            windowsPaths.forEach { path ->
                val dir = File(path)
                if (dir.exists() && dir.isDirectory) {
                    javaExecutables.addAll(
                        dir.listFiles()?.filter { it.isDirectory }
                            ?.mapNotNull { File(it, "bin\\javaw.exe").takeIf { exe -> exe.exists() }?.absolutePath }
                            ?: emptyList(),
                    )
                }
            }
        }
        osName.contains("mac") || osName.contains("darwin") -> {
            val macPaths =
                listOf(
                    "/Library/Java/JavaVirtualMachines",
                    "/usr/local/opt/openjdk",
                )
            macPaths.forEach { path ->
                val dir = File(path)
                if (dir.exists() && dir.isDirectory) {
                    javaExecutables.addAll(
                        dir.listFiles()?.filter { it.isDirectory }
                            ?.mapNotNull { File(it, "Contents/Home/bin/java").takeIf { exe -> exe.exists() }?.absolutePath }
                            ?: emptyList(),
                    )
                }
            }
        }
        osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> {
            val linuxPaths =
                listOf(
                    "/usr/lib/jvm",
                    "/usr/java",
                    "${System.getProperty("user.home")}/.sdkman/candidates/java",
                )
            linuxPaths.forEach { path ->
                val dir = File(path)
                if (dir.exists() && dir.isDirectory) {
                    javaExecutables.addAll(
                        dir.listFiles()?.filter { it.isDirectory }
                            ?.mapNotNull { File(it, "bin/java").takeIf { exe -> exe.exists() }?.absolutePath }
                            ?: emptyList(),
                    )
                }
            }
        }
    }

    return javaExecutables
}
