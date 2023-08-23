pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.51.0"
////                            # available:"0.60.0"
}

include(":lib")
include(":server")
include(":ui")
