import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":common"))
    implementation("com.github.kwhat:jnativehook:2.2.2")
}

tasks.withType<ShadowJar> {
    archiveVersion.set("")
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "kr.hahaha98757.killchain.client.MainKt"
    }
    mergeServiceFiles()
}

tasks.register<Exec>("packageExe") {
    dependsOn("shadowJar")
    if (file("build/jpackage").exists()) file("build/jpackage").deleteRecursively()
    commandLine(
        "jpackage",
        "--type", "app-image",
        "--input", "build/libs",
        "--name", "client",
        "--main-jar", "client.jar",
        "--icon", "icon.ico",
        "--dest", "build/jpackage",
        "--win-console"
    )
    doLast { file("build/jpackage/client/client.ico").delete() }
}