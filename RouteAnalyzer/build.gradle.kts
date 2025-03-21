plugins {
    application
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "org.routeanalyzer"
version = "1.0"

repositories {
    mavenCentral()
}
application {
    mainClass.set("org.routeanalyzer.MainKt")
}
dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib")) // Add Kotlin stdlib
    implementation("org.yaml:snakeyaml:2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.routeanalyzer.MainKt"
    }
}
tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "org.routeanalyzer.MainKt"  // Update with your main class
    }
    // Include the Kotlin runtime in the JAR
    mergeServiceFiles()
    from(sourceSets.main.get().output)
    archiveFileName.set("RouteAnalyzer-1.0-all.jar")
}
kotlin {
    jvmToolchain(23)
}