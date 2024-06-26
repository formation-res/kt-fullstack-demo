buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("multiplatform") apply false
}

subprojects {

    repositories {
        mavenCentral()
//        maven("https://jitpack.io")
        maven("https://maven.tryformation.com/releases") {
            content {
                includeGroup("com.jillesvangurp")
                includeGroup("com.tryformation")
                includeGroup("com.tryformation.fritz2")
            }
        }
    }

//    tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile> {
//        kotlinOptions {
//            jvmTarget = "21"
//            languageVersion = "2.0"
//        }
//    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        testLogging.events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT
        )
        addTestListener(object : TestListener {
            val failures = mutableListOf<String>()
            override fun beforeSuite(desc: TestDescriptor) {
            }

            override fun afterSuite(desc: TestDescriptor, result: TestResult) {
            }

            override fun beforeTest(desc: TestDescriptor) {
            }

            override fun afterTest(desc: TestDescriptor, result: TestResult) {
                if (result.resultType == TestResult.ResultType.FAILURE) {
                    val report =
                        """
                    TESTFAILURE ${desc.className} - ${desc.name}
                    ${
                            result.exception?.let { e ->
                                """
                            ${e::class.simpleName} ${e.message}
                        """.trimIndent()
                            }
                        }
                    -----------------
                    """.trimIndent()
                    failures.add(report)
                }
            }
        })
    }
}