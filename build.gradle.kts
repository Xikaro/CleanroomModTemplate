import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.compiler
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    java
    `java-library`
    `maven-publish`

    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.ideaExt)
    alias(libs.plugins.unimined)
    alias(libs.plugins.buildConstants)
}

// Early Assertions
assertProperty("mod_version")
assertProperty("mod_package")
assertProperty("mod_id")
assertProperty("mod_name")

assertSubProperties("use_tags", "tag_class_name")
assertSubProperties("use_access_transformer", "access_transformer_locations")
assertSubProperties("is_coremod", "coremod_includes_mod", "coremod_plugin_class_name")
assertSubProperties("use_asset_mover", "asset_mover_version")

setDefaultProperty("generate_sources_jar", true, false)
setDefaultProperty("generate_javadocs_jar", true, false)
setDefaultProperty("generate_dev_jar", true, false)
setDefaultProperty("minecraft_username", true, "Developer")
setDefaultProperty("extra_jvm_args", false, "")

version = propertyString("mod_version")
group = propertyString("mod_package")

base {
    archivesName.set(propertyString("mod_id"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.javaToolchain.get())
    }
    if (propertyBool("generate_sources_jar")) {
        withSourcesJar()
    }
    if (propertyBool("generate_javadocs_jar")) {
        withJavadocJar()
    }
}

kotlin {
    jvmToolchain(libs.versions.javaToolchain.get().toInt())
}

configurations {
    val contain by creating
    implementation { extendsFrom(contain) }
    val modCompileOnly by creating
    compileOnly { extendsFrom(modCompileOnly) }
    val modRuntimeOnly by creating
    runtimeOnly { extendsFrom(modRuntimeOnly) }
}

val remapTaskName = if (propertyBool("enable_shadow")) "remapShadowJar" else "remapJar"

unimined.minecraft {
    version(propertyString("minecraft_version"))

    mappings {
        mcp("stable", "39-1.12")
    }

    cleanroom {
        if (propertyBool("use_access_transformer")) {
            accessTransformer("${rootProject.projectDir}/src/main/resources/${propertyString("access_transformer_locations")}")
        }
        loader(propertyString("loader_version"))
        runs.auth.username = propertyString("minecraft_username")
        runs.all {
            val extraArgs = propertyString("extra_jvm_args")
            if (extraArgs.trim().isNotEmpty()) {
                jvmArgs(extraArgs.split("\\s+".toRegex()))
            }
            if (propertyBool("enable_foundation_debug")) {
                systemProperties.apply {
                    set("foundation.dump", "true")
                    set("foundation.verbose", "true")
                }
            }
            if (propertyBool("is_coremod")) {
                systemProperty("fml.coreMods.load", propertyString("coremod_plugin_class_name"))
            }
        }
    }

    defaultRemapJar = false

    val jarTaskName = if (propertyBool("enable_shadow")) "shadowJar" else "jar"

    remap(tasks.named(jarTaskName).get()) {
    }

    mods {
        val modCompileOnly by configurations.getting
        val modRuntimeOnly by configurations.getting
        remap(modCompileOnly)
        remap(modRuntimeOnly)
    }
}

apply(plugin = "dependencies")

tasks.processResources {
    val replaceProperties = mapOf(
        "mod_id" to propertyString("mod_id"),
        "mod_name" to propertyString("mod_name"),
        "mod_version" to propertyString("mod_version"),
        "mod_description" to propertyString("mod_description"),
        "mod_credits" to propertyString("mod_credits"),
        "mod_url" to propertyString("mod_url"),
        "mod_update_json" to propertyString("mod_update_json"),
        "mod_logo_path" to propertyString("mod_logo_path"),
        "mod_authors" to propertyStringList("mod_authors", ",").joinToString("\", \"") { it.trim() },
        "minecraft_version" to propertyString("minecraft_version"),
    )

    inputs.properties(replaceProperties)
    filesMatching(listOf("mcmod.info", "pack.mcmeta")) {
        expand(replaceProperties)
    }

    if (propertyBool("use_access_transformer")) {
        rename("(.+_at.cfg)", "META-INF/$1")
    }
}

tasks.generateBuildConstants.configure {
    classname.set(propertyString("tag_class_name"))
    includePredefinedConstants.set(false)

    additionalConstants.put("MOD_ID", propertyString("mod_id"))
    additionalConstants.put("MOD_VERSION", propertyString("mod_version"))
    additionalConstants.put("MOD_NAME", propertyString("mod_name"))
    additionalConstants.put("SERVER_PROXY", propertyString("tag_server_proxy"))
    additionalConstants.put("CLIENT_PROXY", propertyString("tag_client_proxy"))
}


if (propertyBool("generate_sources_jar")) {
    tasks.named<Jar>("sourcesJar") {
        dependsOn(tasks.named("generateBuildConstants"))
    }
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("generateBuildConstants"))
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaTarget.get()))
}

if (!propertyBool("enable_shadow")) {
    tasks.shadowJar { enabled = false }
}

idea {
    module {
        inheritOutputDirs = true
        isDownloadJavadoc = true
        isDownloadSources = true
    }

    project {
        settings {
            runConfigurations {
                add(Gradle("1. Build").apply {
                    setProperty("taskNames", listOf("build"))
                })
                add(Gradle("2. Run Client").apply {
                    setProperty("taskNames", listOf("runClient"))
                })
                add(Gradle("3. Run Server").apply {
                    setProperty("taskNames", listOf("runServer"))
                })
            }
            compiler.javac {
                afterEvaluate {
                    javacAdditionalOptions = "-encoding utf8"
                }
            }
        }
    }
}

tasks.jar {
    archiveClassifier = if (propertyBool("generate_dev_jar")) "dev" else null
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val contain by configurations.getting
    if (!contain.isEmpty) {
        into("/") {
            from(contain)
        }
    }
    doFirst {
        manifest {
            val attributeMap = mutableMapOf<String, Any>()
            attributeMap["ModType"] = "CRL"
            if (!contain.isEmpty) {
                attributeMap["ContainedDeps"] = contain.joinToString(" ") { it.name }
                attributeMap["NonModDeps"] = true
            }
            if (propertyBool("is_coremod")) {
                attributeMap["FMLCorePlugin"] = propertyString("coremod_plugin_class_name")
                if (propertyBool("coremod_includes_mod")) {
                    attributeMap["FMLCorePluginContainsFMLMod"] = true
                }
            }
            if (propertyBool("use_access_transformer")) {
                attributeMap["FMLAT"] = propertyString("access_transformer_locations")
            }
            attributes(attributeMap)
        }
    }
    finalizedBy(tasks.named(remapTaskName).get())
}

tasks.shadowJar {
    configurations.add(project.configurations.shadow)
    archiveClassifier = "shadow"
}

tasks.named(remapTaskName) {
    doFirst {
        logging.captureStandardOutput(LogLevel.INFO)
    }
    doLast {
        logging.captureStandardOutput(LogLevel.QUIET)
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaTarget.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaTarget.get())
}

tasks.test {
    useJUnitPlatform()
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(libs.versions.javaToolchain.get())
    }

    if (propertyBool("show_testing_output")) {
        testLogging {
            showStandardStreams = true
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

apply(plugin = "publishing")
apply(plugin = "extra")
