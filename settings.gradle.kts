pluginManagement {
    repositories {
        gradlePluginPortal {
            content {
                excludeGroup("org.apache.logging.log4j")
            }
        }
        mavenCentral()
        maven {
            setUrl("https://maven.arcseekers.com/releases")
        }
        maven {
            setUrl("https://maven.minecraftforge.net/")
        }
        maven {
            setUrl("https://maven.fabricmc.net/")
        }
        maven {
            setUrl("https://maven.wagyourtail.xyz/releases")
        }
        maven {
            setUrl("https://maven.wagyourtail.xyz/snapshots")
        }
    }
}

plugins {
    // Automatic toolchain provisioning
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("forge") {
            from(files("gradle/forge.versions.toml"))
        }
    }
}

// Due to an IntelliJ bug, this has to be done
// rootProject.name = archives_base_name
rootProject.name = providers.gradleProperty("mod_name").getOrElse(rootProject.projectDir.name)
