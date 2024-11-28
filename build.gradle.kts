plugins {
    `maven-publish`
    kotlin("jvm") version "2.0.21"
}

repositories {
    mavenCentral()
    mavenLocal()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
