import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization")

}

sourceSets {
    main {
        kotlin {
        }
    }
}

tasks.withType<KotlinCompile> {

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }

}

dependencies {
    implementation(project(":lib"))
    implementation(Kotlin.stdlib.jdk8)
    implementation(KotlinX.coroutines.jdk8)
    implementation(KotlinX.serialization.json)
    implementation(Ktor.server.core)
    implementation(Ktor.server.netty)

    implementation("io.ktor:ktor-server-status-pages:_")
    implementation("io.ktor:ktor-server-default-headers:_")
    implementation("io.ktor:ktor-server-content-negotiation:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")
    implementation("io.ktor:ktor-server-cors:_")

    implementation(Koin.core)
    implementation( "io.insert-koin:koin-ktor:_")
    implementation( "io.insert-koin:koin-logger-slf4j:_")

    implementation("ch.qos.logback:logback-classic:_")
}

