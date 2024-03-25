rootProject.name = "takeItEasy"

include(
    "boot",
    "takeItEasy-presentation",
    "takeItEasy-application",
    "takeItEasy-domain",
    "takeItEasy-batch",
    "takeItEasy-aop"
    )

pluginManagement {
    val kotlinVersion :String by settings
    val springDependencyManagerVersion :String by settings
    val springBootVersion :String by settings

    resolutionStrategy{
        eachPlugin{
            when (requested.id.id){
                "io.spring.dependency-management" -> useVersion(springDependencyManagerVersion)
                "org.springframework.boot" -> useVersion(springBootVersion)
                "org.jetbrains.kotlin.jvm" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.spring" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.jpa" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.allopen" -> useVersion(kotlinVersion)
                "org.gradle.core" -> useVersion(kotlinVersion)

            }
        }
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}