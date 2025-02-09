import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.kotlinxSerialization)

  alias(libs.plugins.jetbrainsCompose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  jvm("desktop")

  sourceSets {
    val desktopMain by getting
    val desktopTest by getting

    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(libs.ktor.client.core)
      implementation(libs.kotlinx.serialization.json)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.coroutines.swing)
      implementation(libs.voyager.navigator)
      implementation(libs.voyager.screenModel)
      implementation(libs.voyager.koin)
      implementation(libs.koin.core)
      implementation(libs.koin.compose)
      implementation(libs.slf4j.simple)
    }
    desktopMain.dependencies {
      implementation(compose.desktop.currentOs)
      implementation(libs.ktor.client.cio)
      implementation(libs.azuljavadownloader)

      // all files in libs folder
      implementation(fileTree("../libs") {
        include("**/*.jar")
      })
    }

    desktopTest.dependencies {
      implementation(kotlin("test-junit"))
      implementation(libs.kotlin.test)
      implementation(libs.kotest.framework.engine)
      implementation(libs.kotest.framework.datatest)
      implementation(libs.kotest.assertions.core)
      implementation(libs.kotest.runner.junit5)
      implementation("junit:junit:4.13.2")
    }
  }
}

compose.desktop {
  application {
    mainClass = "MainKt"

    buildTypes.release.proguard {
      obfuscate.set(true)
      configurationFiles.from(project.file("compose-desktop.pro"))
    }

    nativeDistributions {
      modules("jdk.zipfs", "jdk.unsupported")

      targetFormats(TargetFormat.Dmg, TargetFormat.Deb, TargetFormat.Exe, TargetFormat.Msi)
      packageName = "DBC Brasil Launcher"
      packageVersion = "1.0.0"

      val applicationIconFile = project.file("src/desktopMain/resources/icon.ico")
      if (!applicationIconFile.exists()) {
        throw IllegalArgumentException("Icon file not found")
      }

      println(applicationIconFile)

      linux {
        iconFile.set(applicationIconFile)
      }
      windows {
        shortcut = true
        menu = true
        iconFile.set(applicationIconFile)
      }
    }
  }
}

