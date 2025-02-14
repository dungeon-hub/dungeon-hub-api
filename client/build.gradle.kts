import net.thebugmc.gradle.sonatypepublisher.PublishingType

plugins {
    id("java-library")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher").version("1.2.3")
    kotlin("jvm")
    id("com.google.devtools.ksp").version("2.1.0-1.0.29")
}

group = "net.dungeon-hub.api"
val artifactId = "client"
version = "0.5.4"
description = "A client library written in Kotlin to simplify the integration of the Dungeon Hub API."

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
    //Model classes
    api(project(":model"))

    //Logging
    api(libs.slf4j2.api)

    //HTTP Client
    api(libs.okhttp)

    //Used frameworks for compatible classes
    implementation(libs.kord.extensions)

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