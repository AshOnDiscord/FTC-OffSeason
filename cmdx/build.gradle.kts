import org.gradle.kotlin.dsl.testImplementation
plugins {
    kotlin("jvm")
    `maven-publish`
    id("org.jetbrains.dokka") version "2.0.0"
    id("io.gitlab.arturbosch.detekt") version ("1.23.8")
}

group = "com.millburnx"
version = "0.1.2"
repositories {
    mavenLocal()
    mavenCentral()
}
dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
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

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar.get())

            groupId = "com.millburnx"
            artifactId = "cmdx"
            version = "0.1.2"

            pom {
                name = "CmdX"
                description = "A FTC Orientated Kotlin library for creating complex performant command-based systems"
                url = "https://github.com/AshOnDiscord/FTC-OffSeason"
            }
        }
    }
}
