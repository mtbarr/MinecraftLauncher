plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    jvm("desktop")

    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget =
        when {
            hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
            hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
            hostOs == "Linux" && isArm64 -> linuxArm64("native")
            hostOs == "Linux" && !isArm64 -> linuxX64("native")
            isMingwX64 -> mingwX64("native")
            else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
        }

//    nativeTarget.apply {
//        binaries {
//            executable {
//                entryPoint = "main"
//            }
//        }
//    }

    sourceSets {
        val desktopMain by getting
        val desktopTest by getting

        val nativeMain by getting
        val nativeTest by getting

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.io)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
        }

        nativeMain.dependencies {
            implementation(libs.kotlinx.io)
            implementation(libs.okio)
        }
    }
}
