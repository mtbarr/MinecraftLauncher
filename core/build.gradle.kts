plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        val desktopTest by getting

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.io)
            implementation(libs.ktor.client.core)
        }

        desktopMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }
}
