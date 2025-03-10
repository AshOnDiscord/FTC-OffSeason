plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "2.0.0"
    id("io.gitlab.arturbosch.detekt") version ("1.23.8")
}

group = "com.millburnx"
version = "0.10"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
    explicitApi()
}

detekt {
    config.from(files("$rootDir/cmdx/detekt.yml"))
    source.from(files("src/main/kotlin"))
}
