package launcher

import fr.flowarg.flowlogger.ILogger
import fr.flowarg.flowlogger.Logger
import fr.flowarg.flowupdater.FlowUpdater
import fr.flowarg.flowupdater.versions.VanillaVersion
import fr.flowarg.openlauncherlib.NoFramework
import fr.theshark34.openlauncherlib.JavaUtil
import fr.theshark34.openlauncherlib.minecraft.AuthInfos
import fr.theshark34.openlauncherlib.minecraft.GameFolder
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator
import fr.theshark34.openlauncherlib.util.Saver
import kotlinx.coroutines.flow.MutableStateFlow
import launcher.config.LauncherConfig
import java.io.File
import java.nio.file.Path
import java.util.UUID

import fr.flowarg.azuljavadownloader.AzulJavaDownloader
import fr.flowarg.azuljavadownloader.RequestedJavaInfo
import fr.flowarg.azuljavadownloader.AzulJavaType
import fr.flowarg.azuljavadownloader.AzulJavaOS
import fr.flowarg.azuljavadownloader.AzulJavaArch
import fr.flowarg.flowupdater.versions.forge.ForgeVersionBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import launcher.state.DownloadingState
import launcher.state.LauncherState
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class Launcher {

  companion object {
    const val MODS_URL: String = "https://raw.githubusercontent.com/dbcbrasil/modpack/refs/heads/main/modpack.json"

    const val OPTIONS_URL = "https://github.com/dbcbrasil/modpack/raw/refs/heads/main/options.txt"
    const val OPTIONS_OPTIFINE_URL = "https://github.com/dbcbrasil/modpack/raw/refs/heads/main/optionsof.txt"
    const val SERVER_FILE_URL = "https://github.com/dbcbrasil/modpack/raw/refs/heads/main/servers.dat"
    const val RESOURCEPACK_URL = "https://codeberg.org/sasuke/dbc-heroes-modpack/raw/branch/main/DBC%20Brasil.zip"

    val GAME_DIR: Path = GameDirGenerator.createGameDir("dbcbrasil", false)
    val SERVERS_DAT_FILE: File = File(GAME_DIR.toFile(), "servers.dat")
    val RESOURCE_PACKS_DIR: File = File(GAME_DIR.toFile(), "resourcepacks")
    val OPTIONS_FILE: File = File(GAME_DIR.toFile(), "options.txt")
    val OPTIONS_OPTIFINE_FILE: File = File(GAME_DIR.toFile(), "optionsof.txt")


    val LOGS_FILE: File = File("${GAME_DIR.toFile()}${File.separator}logs.txt")
    val CONFIG: Saver = Saver(File("${GAME_DIR.toFile()}${File.separator}launcher_config.properties").toPath())
    val LOGGER: ILogger = Logger("[DBCBR]", LOGS_FILE.toPath())
  }


  private val launcherScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  val currentLauncherConfig: MutableStateFlow<LauncherConfig> = MutableStateFlow(LauncherConfig())
  val launcherConfig: LauncherConfig
    get() = currentLauncherConfig.value

  val currentDownloadingState: MutableStateFlow<DownloadingState> = MutableStateFlow(DownloadingState())


  private val _launcherState: MutableStateFlow<LauncherState> = MutableStateFlow(LauncherState.Idle)
  val launcherState: MutableStateFlow<LauncherState>
    get() = _launcherState

  fun preInit() {
    // Executa o download do Java de forma assíncrona
    launcherScope.launch {
      launcherState.tryEmit(LauncherState.Preparing)
      LOGGER.info("LauncherState=${launcherState.value}")

      LOGGER.info("Carregando configuração inicial..")
      ensureGameDir()
      downloadServersDat()
      downloadResourcepack()
      downloadOptionsFile()
      downloadOptionsOptifineFile()

      CONFIG.load()


      val javaPath = ensureJavaInstallation()

      currentLauncherConfig.emit(
        LauncherConfig(
          launcherVersion = CONFIG["launcherVersion"]?.toInt() ?: 1,
          userName = CONFIG["userName"] ?: "",
          maxRam = CONFIG["maxRam"]?.toInt() ?: 2048,
          rememberUserName = CONFIG["rememberUserName"]?.toBoolean() ?: false,
          javaPath = CONFIG["javaPath"] ?: javaPath
        )
      )

      JavaUtil.setJavaCommand(launcherConfig.javaPath)
      LOGGER.info("Configuração carregada: ${currentLauncherConfig.value}")

      launcherState.emit(LauncherState.Idle)
      LOGGER.info("LauncherState=${launcherState.value}")
    }
  }


  fun saveConfig() {
    CONFIG["launcherVersion"] = currentLauncherConfig.value.launcherVersion.toString()
    CONFIG["userName"] = currentLauncherConfig.value.userName
    CONFIG["maxRam"] = currentLauncherConfig.value.maxRam.toString()
    CONFIG["rememberUserName"] = currentLauncherConfig.value.rememberUserName.toString()
    CONFIG["javaPath"] = currentLauncherConfig.value.javaPath

    CONFIG.save()

    LOGGER.info("Configuração salva: ${currentLauncherConfig.value}")
  }


  fun launchGame() {
    LOGGER.info("Java Utilizado: ${JavaUtil.getJavaCommand()}")
    LOGGER.info("Launching game...")

    // Inicia uma coroutine no escopo global
    launcherScope.launch {
      runCatching {

        launcherState.tryEmit(LauncherState.Updating)
        update()


        NoFramework.ModLoader.OLD_FORGE.setJsonFileNameProvider { _, _ -> "1.7.10-Forge10.13.4.1614-1.7.10.json" }
        val noFramework = NoFramework(
          GAME_DIR,
          AuthInfos(
            launcherConfig.userName,
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
          ),
          GameFolder.FLOW_UPDATER
        )

        val minRam = if (launcherConfig.maxRam < 512) 512 else launcherConfig.maxRam
        val maxRam = if (launcherConfig.maxRam < 1024) 1024 else launcherConfig.maxRam
        noFramework.additionalArgs.addAll(
          listOf(
            "-Xms${minRam}M",
            "-Xmx${maxRam}M",
          )
        )

        LOGGER.info("Launching game with username: ${launcherConfig?.userName}")

        val proc = noFramework.launch(
          "1.7.10",
          "1.7.10-Forge10.13.4.1614-1.7.10",
          NoFramework.ModLoader.OLD_FORGE
        )

        // Atualiza o estado para Running
        launcherState.tryEmit(LauncherState.Running)
        LOGGER.info("LauncherState=${launcherState.value}")

        // Espera o processo terminar
        proc.waitFor()

        // Atualiza o estado para Idle quando o processo terminar
        launcherState.tryEmit(LauncherState.Idle)
        LOGGER.info("LauncherState=${launcherState.value}")
      }.onFailure {
        it.printStackTrace()
        launcherState.tryEmit(LauncherState.Idle)
      }
    }
  }

  fun update() {
    handleUpdate()
  }

  private fun handleUpdate() {
    launcherState.tryEmit(LauncherState.Updating)
    LOGGER.info("LauncherState=${launcherState.value}")

    val vanillaVersion = VanillaVersion.VanillaVersionBuilder()
      .withName("1.7.10")
      .build()

    val forgeVersion = ForgeVersionBuilder()
      .withForgeVersion("1.7.10-10.13.4.1614-1.7.10")

      .withMods(MODS_URL)
      .build()

    val updater = FlowUpdater.FlowUpdaterBuilder()
      .withVanillaVersion(vanillaVersion)
      .withModLoaderVersion(forgeVersion)
      .withLogger(LOGGER)
      .build()

    updater.update(GAME_DIR)

    LOGGER.info("LauncherState=${launcherState.value}")
  }

  /**
   * Verifica se o Java já está instalado no diretório de instalação.
   * Caso não esteja, baixa e instala o Java 8 Zulu usando a AzulJavaDownloader.
   * Retorna o caminho absoluto do executável do Java.
   */
  private suspend fun ensureJavaInstallation(): String {
    val javaDir = File(GAME_DIR.toFile(), "java")

    val javaHome =
      javaDir.listFiles()?.firstOrNull { it.isDirectory && File(it, "bin${File.separator}java.exe").exists() }

    if (javaHome == null) {
      launcherState.emit(LauncherState.DownloadingJava) // Atualiza o estado para DownloadingJava
      LOGGER.info("LauncherState=${launcherState.value}")

      downloadJava(javaDir) // Executa o download de forma assíncrona

      val downloadedJavaHome =
        javaDir.listFiles()?.firstOrNull { it.isDirectory && File(it, "bin${File.separator}java.exe").exists() }
      if (downloadedJavaHome == null) {
        launcherState.emit(LauncherState.Idle) // Garante que o estado volte ao normal em caso de erro
        throw IllegalStateException("Erro ao baixar o Java. Nenhum Java encontrado em ${javaDir.absolutePath}")
      }

      launcherState.emit(LauncherState.Idle) // Atualiza o estado para Idle após o download
      LOGGER.info("LauncherState=${launcherState.value}")

      return File(downloadedJavaHome, "bin${File.separator}java.exe").absolutePath
    }

    return File(javaHome, "bin${File.separator}java.exe").absolutePath
  }

  /**
   * Utiliza a AzulJavaDownloader para baixar e instalar o Java 8 Zulu.
   */
  private fun downloadJava(targetDir: File) {
    try {
      val downloader = AzulJavaDownloader()


      if (!targetDir.exists()) {
        targetDir.mkdirs()
      }
      val osName = System.getProperty("os.name").toLowerCase()
      val os = when {
        osName.contains("win") -> AzulJavaOS.WINDOWS
        osName.contains("mac") -> AzulJavaOS.MACOS
        else -> AzulJavaOS.LINUX
      }
      // Assumindo arquitetura x64
      val arch = AzulJavaArch.X64
      // Configura a versão 8 do Java (ZULU)
      val requestedInfo = RequestedJavaInfo("8", AzulJavaType.JDK, os, arch)
      val buildInfo = downloader.getBuildInfo(requestedInfo)
      val javaHome = downloader.downloadAndInstall(buildInfo, targetDir.toPath())
      LOGGER.info("Java instalado em: ${javaHome.toAbsolutePath()}")

    } catch (ex: Exception) {
      LOGGER.warn("Erro ao baixar o Java: ${ex.message}")
    }
  }


  private fun ensureGameDir() {
    if (!GAME_DIR.toFile().exists()) {
      GAME_DIR.toFile().mkdirs()
    }
  }

  private fun downloadResourcepack() {
    val resourcePacksFolderExist = RESOURCE_PACKS_DIR.exists()
    val resourcePacksFolderIsEmpty = RESOURCE_PACKS_DIR.listFiles()?.isEmpty() ?: true

    if (!resourcePacksFolderExist || resourcePacksFolderIsEmpty) {
      try {
        RESOURCE_PACKS_DIR.mkdirs()// Cria a pasta caso não exista

        LOGGER.info("Baixando resourcepack de $RESOURCEPACK_URL...")
        val url = URL(RESOURCEPACK_URL)
        url.openStream().use { input ->
          Files.copy(input, File(RESOURCE_PACKS_DIR, "DBC Brasil.zip").toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        LOGGER.info("Download do resourcepack concluído!")
      } catch (e: Exception) {
        LOGGER.warn("Erro ao baixar resourcepack: ${e.message}")
      }
    } else {
      LOGGER.info("resourcepack já existe, pulando o download.")
    }
  }

  private fun downloadServersDat() {
    if (!SERVERS_DAT_FILE.exists()) {
      try {
        LOGGER.info("Baixando servers.dat de $SERVERS_DAT_FILE...")
        val url = URL(SERVER_FILE_URL)
        url.openStream().use { input ->
          Files.copy(input, SERVERS_DAT_FILE.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        LOGGER.info("Download do servers.dat concluído!")
      } catch (e: Exception) {
        LOGGER.warn("Erro ao baixar servers.dat: ${e.message}")
      }
    } else {
      LOGGER.info("servers.dat já existe, pulando o download.")
    }
  }

  private fun downloadOptionsFile() {
    if (!OPTIONS_FILE.exists()) {
      try {
        LOGGER.info("Baixando options.txt de $OPTIONS_URL...")
        val url = URL(OPTIONS_URL)
        url.openStream().use { input ->
          Files.copy(input, OPTIONS_FILE.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        LOGGER.info("Download do options.txt concluído!")
      } catch (e: Exception) {
        LOGGER.warn("Erro ao baixar options.txt: ${e.message}")
      }
    } else {
      LOGGER.info("options.txt já existe, pulando o download.")
    }
  }

  private fun downloadOptionsOptifineFile() {
    if (!OPTIONS_OPTIFINE_FILE.exists()) {
      try {
        LOGGER.info("Baixando optionsof.txt de $OPTIONS_OPTIFINE_URL...")
        val url = URL(OPTIONS_OPTIFINE_URL)
        url.openStream().use { input ->
          Files.copy(input, OPTIONS_OPTIFINE_FILE.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        LOGGER.info("Download do optionsof.txt concluído!")
      } catch (e: Exception) {
        LOGGER.warn("Erro ao baixar optionsof.txt: ${e.message}")
      }
    } else {
      LOGGER.info("optionsof.txt já existe, pulando o download.")
    }
  }
}
