import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "kr.hahaha98757.killchain"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.kwhat:jnativehook:2.2.2")
}

val serverMainClass = "kr.hahaha98757.killchain.server.MainServerKt"
val clientMainClass = "kr.hahaha98757.killchain.client.MainClientKt"
val serverPackage = "kr/hahaha98757/killchain/server/**"
val clientPackage = "kr/hahaha98757/killchain/client/**"
val buildFolder = file("${layout.buildDirectory.locationOnly.get()}/libs/KillChain-v$version")

tasks.register<ShadowJar>("buildServer") {
    destinationDirectory.set(buildFolder)
    archiveBaseName.set("server")
    archiveVersion.set("")
    archiveClassifier.set("")
    manifest { attributes["Main-Class"] = serverMainClass }
    from(sourceSets.main.get().output) { exclude(clientPackage) }
    configurations = listOf(project.configurations.runtimeClasspath.get())
}

tasks.register<ShadowJar>("buildClient") {
    destinationDirectory.set(buildFolder)
    archiveBaseName.set("client")
    archiveVersion.set("")
    archiveClassifier.set("")
    manifest { attributes["Main-Class"] = clientMainClass }
    from(sourceSets.main.get().output) { exclude(serverPackage) }
    configurations = listOf(project.configurations.runtimeClasspath.get())
}

tasks.named<Jar>("jar") {
    isEnabled = false
    finalizedBy("copyResources")
}

tasks.named("build") {
    dependsOn("buildServer", "buildClient")
}

tasks.register<Copy>("copyResources") {
    from("resources")
    into(buildFolder)
}