plugins {
    id("java-library")
    id("chirp-backend.spring-boot-service")
    kotlin("plugin.jpa")
}

group = "com.holparb"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.common)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}