plugins {
    java
}

repositories {
    // Other repositories described by default:
    // CleanroomMC: https://maven.cleanroommc.com
    mavenCentral()
    maven {
        name = "CurseMaven"
        setUrl("https://cursemaven.com")
    }
    maven {
        name = "CleanroomCurseMaven"
        setUrl("https://curse.cleanroommc.com")
    }
    maven {
        name = "Modrinth"
        setUrl("https://api.modrinth.com/maven")
    }
    maven {
        name = "CleanroomMaven"
        setUrl("https://maven.cleanroommc.com")
    }
    mavenCentral()
    mavenLocal() // Must be last for caching to work
}

dependencies {
    val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    val forge = project.extensions.getByType<VersionCatalogsExtension>().named("forge")

    compileOnly(libs.findLibrary("jetbrainsAnnotations").get())
    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())

    implementation(libs.findLibrary("forgelin").get())

    if (propertyBool("enable_junit_testing")) {
        testImplementation(libs.findLibrary("junit").get())
        testRuntimeOnly(libs.findLibrary("junitLauncher").get())
        testCompileOnly(libs.findLibrary("lombok").get())
        testAnnotationProcessor(libs.findLibrary("lombok").get())
    }

    if (propertyBool("enable_lwjglx")) {
        compileOnly(libs.findLibrary("lwjglx").get())
    }

    if (propertyBool("use_asset_mover")) {
        implementation(forge.findLibrary("assetMover").get())
    }

    // Example - Dependency descriptor:
    // "com.google.code.gson:gson:2.8.6" -> group: com.google.code.gson, name: gson, version: 2.8.6
    // "group:name:version:classifier" where classifier is optional

    // Example - CurseMaven dependencies:
    // "curse.maven:had-enough-items-557549:4543375" -> had-enough-items = project slug, 557549 = project id, 4543375 = file id
    // Full documentation: https://cursemaven.com/

    // Example - Modrinth dependencies:
    // "maven.modrinth:jei:4.16.1.1000" -> jei = project name, 4.16.1.1000 = file version
    // Full documentation: https://docs.modrinth.com/docs/tutorials/maven/

    // Common dependency types (configuration):
    // implementation = dependency available at both compile time and runtime
    // runtimeOnly = runtime dependency
    // compileOnly = compile time dependency
    // annotationProcessor = annotation processing dependencies
    // contain = bundle dependency jars into final artifact, will extract them in mod loading. Can be used it as jar-in-jar
    // shadow = bundle dependencies into shadow output artifact (relocation configurable in shadowJar task)
    // modImplementation = mod dependency available at both compile time and runtime
    // modCompileOnly = mod dependency available only at compile time
}
