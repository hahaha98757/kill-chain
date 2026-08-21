import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.4.3"
}

dependencies {
    implementation(project(":common"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

tasks.withType<ShadowJar> {
    archiveVersion.set("")
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "kr.hahaha98757.killchain.server.MainKt"
    }
    mergeServiceFiles()
}

tasks.register<Exec>("packageExe") {
    group = "build"
    description = "Packages the application into an executable format using JPackage."
    dependsOn("shadowJar")
    delete("build/jpackage")
    commandLine(
        "jpackage",
        "--type", "app-image",
        "--input", "build/libs",
        "--name", "server",
        "--main-jar", "server.jar",
        "--icon", "icon.ico",
        "--dest", "build/jpackage",
        "--win-console",
        "--java-options", "--enable-native-access=ALL-UNNAMED"
    )
    doLast { file("build/jpackage/server/server.ico").delete() }
}