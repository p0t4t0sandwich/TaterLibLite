pluginManagement {
    repositories {
         maven("https://maven.neuralnexus.dev/mirror")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

rootProject.name = "taterlib_lite"

include(":base")
