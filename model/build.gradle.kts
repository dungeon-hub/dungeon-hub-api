import net.thebugmc.gradle.sonatypepublisher.PublishingType

plugins {
    id("java-library")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher").version("1.2.3")
    kotlin("jvm")
    id("com.google.devtools.ksp").version("2.0.21-1.0.26")
    id("dev.kordex.gradle.kordex") version "1.5.8"
}

group = "net.dungeon-hub.api"
val artifactId = "model"
version = "0.5.2"
description = "The model classes that are used in the Dungeon Hub API."

kordEx {
    kordExVersion = libs.kord.extensions.get().version
    jvmTarget = 17

    i18n {
        classPackage = "net.dungeonhub.api.model.i18n"
        translationBundle = "dh-api.strings"
    }
}

dependencies {
    //Moshi, the JSON library
    api("com.squareup.moshi:moshi-kotlin:1.15.1")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")

    //Gson, used for compatibility purposes
    api("com.google.code.gson:gson:2.11.0")

    //Used frameworks for compatible classes
    implementation("org.springframework:spring-web:6.1.12")

    //Tests
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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