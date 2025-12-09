plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.21"
    `maven-publish`
    id("org.jetbrains.dokka") version "2.0.0"
    id("io.gitlab.arturbosch.detekt") version ("1.23.8")
}

group = "com.millburnx"
version = "0.1.0"

repositories {
    mavenLocal()
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
    config.from(files("$rootDir/util/detekt.yml"))
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
            artifactId = "util"
            version = "0.1.0"

            pom {
                name = "Util"
                description = "Util functionality"
                url = "https://github.com/AshOnDiscord/FTC-OffSeason"
            }
        }
    }
}
