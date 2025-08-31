import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.20" apply false
}

allprojects {
    group = "kr.hahaha98757.killchain"
    version = "1.1.0"
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    tasks.named<Jar>("jar") { enabled = false }
}

val packageFolder = file("build/KillChain-$version")

tasks.register("build") {
    if (packageFolder.exists()) packageFolder.deleteRecursively()
    packageFolder.mkdirs()
    dependsOn(":client:packageExe", ":server:packageExe")

    doLast {
        copy {
            from(file("client/build/jpackage"))
            into(packageFolder)
        }
        copy {
            from(file("server/build/jpackage"))
            into(packageFolder)
        }
    }
}