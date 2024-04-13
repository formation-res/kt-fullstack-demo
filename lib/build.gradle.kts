@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")

}

kotlin {
    jvm {
    }
    // our search library only supports IR
    js(IR) {
        nodejs {
            testTask {
                useMocha {
                    // javascript is a lot slower than Java, we hit the default timeout of 2000
                    timeout = "20000"
                }
            }
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                api(kotlin("stdlib-common", "_"))
                api(KotlinX.serialization.json)
                api("com.jillesvangurp:search-client:_")
                api("com.jillesvangurp:search-dsls:_")
                api("com.jillesvangurp:json-dsl:_")
                api("io.github.microutils:kotlin-logging:_")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common", "_"))
                implementation(kotlin("test-annotations-common", "_"))
                implementation(Testing.kotest.assertions.core)

            }
        }
        jvmMain {
            dependencies {
            }
        }
        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5", "_"))
                implementation("ch.qos.logback:logback-classic:_")

                implementation(Testing.junit.jupiter.api)
                implementation(Testing.junit.jupiter.engine)
            }
        }
        jsMain {
            dependencies {
            }
        }
        jsTest {
            dependencies {
                implementation(kotlin("test-js", "_"))
            }
        }
    }
}
