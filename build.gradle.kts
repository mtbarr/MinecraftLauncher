import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.kotestMultiplatform) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    val libs = rootProject.libs
    listOf(
        libs.plugins.kotestMultiplatform,
        libs.plugins.ktlint,
        libs.plugins.kotlinxSerialization,
    ).forEach { plugin ->
        apply(plugin = plugin.get().pluginId)
    }

    configure<KtlintExtension> {
        debug.set(true)
    }
}
