plugins {
    `maven-publish`
    kotlin("jvm") version "2.1.0"
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
