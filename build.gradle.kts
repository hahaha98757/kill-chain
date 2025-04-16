import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "kr.hahaha98757.killchain"
version = "1.0.1"

java { toolchain.languageVersion.set(JavaLanguageVersion.of(17)) }

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
val packageFolder = file("build/jpackage/KillChain-$version")

tasks.named<Jar>("jar") { isEnabled = false }

tasks.register<ShadowJar>("buildServer") {
    destinationDirectory.set(file("build/libs/server"))
    archiveBaseName.set("server")
    archiveVersion.set("")
    archiveClassifier.set("")
    manifest { attributes["Main-Class"] = serverMainClass }
    from(sourceSets.main.get().output) { exclude(clientPackage) }
    configurations = listOf(project.configurations.runtimeClasspath.get())
}

tasks.register<ShadowJar>("buildClient") {
    destinationDirectory.set(file("build/libs/client"))
    archiveBaseName.set("client")
    archiveVersion.set("")
    archiveClassifier.set("")
    manifest { attributes["Main-Class"] = clientMainClass }
    from(sourceSets.main.get().output) { exclude(serverPackage) }
    configurations = listOf(project.configurations.runtimeClasspath.get())
}

tasks.register<Exec>("packageExeServer") {
    commandLine(
        "jpackage",
        "--type", "app-image",
        "--input", "build/libs/server",
        "--name", "server",
        "--main-jar", "server.jar",
        "--icon", "serverIcon.ico",
        "--dest", "build/jpackage/KillChain-$version",
        "--win-console"
    )
    doLast { file("build/jpackage/KillChain-$version/server/server.ico").delete() }
}

tasks.register<Exec>("packageExeClient") {
    commandLine(
        "jpackage",
        "--type", "app-image",
        "--input", "build/libs/client",
        "--name", "client",
        "--main-jar", "client.jar",
        "--icon", "clientIcon.ico",
        "--dest", "build/jpackage/KillChain-$version",
        "--win-console"
    )
    doLast { file("build/jpackage/KillChain-$version/client/client.ico").delete() }
}

tasks.named("build") {
    if (!packageFolder.exists()) packageFolder.mkdirs()
    dependsOn("buildServer", "buildClient")
    finalizedBy("packageExeServer", "packageExeClient")
}