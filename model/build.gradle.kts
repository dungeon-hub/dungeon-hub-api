import net.thebugmc.gradle.sonatypepublisher.PublishingType

plugins {
    id("java-library")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher").version("1.2.3")
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp").version("2.0.21-1.0.26")
}

group = "net.dungeon-hub.api"
val artifactId = "model"
version = "0.1.1"
description = "The model classes that are used in the Dungeon Hub API."

repositories {
    mavenCentral()

    maven {
        url = uri("https://repo.kordex.dev/releases")
        name = "KordEx (Releases)"
    }
    maven {
        url = uri("https://repo.kordex.dev/snapshots")
        name = "KordEx (Snapshots)"
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        name = "Sonatype Snapshots (Legacy)"
    }
}

dependencies {
    //Moshi, the JSON library
    api("com.squareup.moshi:moshi-kotlin:1.15.1")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")

    //Gson, used for compatibility purposes
    api("com.google.code.gson:gson:2.11.0")

    //Used frameworks for compatible classes
    implementation("dev.kordex:kord-extensions:2.2.1-SNAPSHOT")
    implementation("org.springframework:spring-web:6.1.12")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlin("test"))
}

centralPortal {
    name = artifactId
    publishingType = PublishingType.USER_MANAGED

    pom {
        name = artifactId
        description = project.description
        url = "https://github.com/dungeon-hub/dungeon-hub-api"

        organization {
            name = "Dungeon Hub"
            url = "https://dungeon-hub.net/"
        }

        scm {
            url = "https://github.com/dungeon-hub/dungeon-hub-api"
            connection = "scm:git://github.com:dungeon-hub/dungeon-hub-api.git"
            developerConnection = "scm:git://github.com:dungeon-hub/dungeon-hub-api.git"
        }

        developers {
            developer {
                id = "taubsie"
                name = "Taubsie"
                email = "taubsie@dungeon-hub.net"
                url = "https://github.com/Taubsie/"
                organizationUrl = "https://dungeon-hub.net/"
            }
        }

        licenses {
            license {
                name = "GPL-3.0"
                url = "https://www.gnu.org/licenses/gpl-3.0"
                distribution = "https://www.gnu.org/licenses/gpl-3.0.txt"
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}