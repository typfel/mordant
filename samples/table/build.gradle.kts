import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    application
}

kotlin {
    jvm()
    macosArm64()
    macosX64()
    linuxX64()
    mingwX64()

    targets.filterIsInstance<KotlinNativeTarget>().forEach { target ->
        target.binaries.executable {
            entryPoint = "com.github.jaocb.demo.main"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation(project(":mordant"))
            }
        }
    }
}

application {
    mainClass.set("com.github.ajalt.mordant.samples.MainKt")
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=utf-8")
}
