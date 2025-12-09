plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
    id("org.jetbrains.dokka") version "2.0.0"
    id("io.gitlab.arturbosch.detekt") version ("1.23.8")
}

group = "com.millburnx"
version = "0.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://mymaven.bylazar.com/releases")
}

android {
    namespace = "com.millburnx.cmdxpedro"

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    kotlin {
        jvmToolchain(17)
    }

    compileSdk = 34
}


dependencies {
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    compileOnly("com.pedropathing:ftc:2.0.3")
    compileOnly(project(":cmdx"))

    compileOnly("org.firstinspires.ftc:Inspection:11.0.0")
    compileOnly("org.firstinspires.ftc:Blocks:11.0.0")
    compileOnly("org.firstinspires.ftc:RobotCore:11.0.0")
    compileOnly("org.firstinspires.ftc:RobotServer:11.0.0")
    compileOnly("org.firstinspires.ftc:OnBotJava:11.0.0")
    compileOnly("org.firstinspires.ftc:Hardware:11.0.0")
    compileOnly("org.firstinspires.ftc:FtcCommon:11.0.0")
    compileOnly("org.firstinspires.ftc:Vision:11.0.0")
    compileOnly("androidx.appcompat:appcompat:1.2.0")

    implementation(project(":pedroparser"))
}

kotlin {
    jvmToolchain(21)
    explicitApi()
}


detekt {
    config.from(files("$rootDir/cmdxpedro/detekt.yml"))
    source.from(files("src/main/kotlin"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.millburnx"
                artifactId = "cmdxpedro"
                version = "0.1.0"

                pom {
                    name = "CmdX Pedro"
                    description = "Pedro Plugin for CmdX"
                    url = "https://github.com/AshOnDiscord/FTC-OffSeason"
                }
            }
        }
    }
}