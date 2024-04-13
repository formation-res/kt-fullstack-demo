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
        commonMain {
            dependencies {
                implementation("com.tryformation.fritz2:core:_")
                implementation("com.tryformation.fritz2:headless:_")
                implementation("com.jillesvangurp:search-client:_")
                implementation(KotlinX.serialization.json)
                implementation(Koin.core)
                implementation(project(":lib"))
            }
        }
        jsMain {
            dependencies {
                // tailwind
                implementation(npm("tailwindcss", "_"))
                implementation(npm("@tailwindcss/forms", "_"))

                // fluent-js
                implementation("com.tryformation:fluent-kotlin:_")

                // webpack
                implementation(devNpm("postcss", "_"))
                implementation(devNpm("postcss-loader", "_"))
                implementation(devNpm("autoprefixer", "_"))
                implementation(devNpm("css-loader", "_"))
                implementation(devNpm("style-loader", "_"))
                implementation(devNpm("cssnano", "_"))            }
        }
    }
}