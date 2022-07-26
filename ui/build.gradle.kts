@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        browser()
    }.binaries.executable()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.ExperimentalStdlibApi")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation("dev.fritz2:core:_")
                implementation("dev.fritz2:headless:_")
                implementation("com.github.jillesvangurp.kt-search:search-client:_")
                implementation(KotlinX.serialization.json)
                implementation(Koin.core)
                implementation(project(":lib"))
            }
        }
        val jsMain by getting {
            dependencies {
                // tailwind
                implementation(npm("tailwindcss", "3.0.19"))
                implementation(npm("@tailwindcss/forms", "0.4.0"))

                // fluent-js
                implementation("com.github.formation-res:fluent-kotlin:_")
//                implementation(npm("@fluent/bundle", "0.17.1"))
//                implementation(npm("@fluent/sequence", "0.7.0"))

                // webpack
                implementation(devNpm("postcss", "8.4.6"))
                implementation(devNpm("postcss-loader", "6.2.1"))
                implementation(devNpm("autoprefixer", "10.4.2"))
                implementation(devNpm("css-loader", "6.6.0"))
                implementation(devNpm("style-loader", "3.3.1"))
                implementation(devNpm("cssnano", "5.0.17"))            }
        }
    }
}


// Fixes webpack-cli incompatibility by pinning the newest version.
// https://youtrack.jetbrains.com/issue/KTIJ-22030
rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
}
