plugins {
    id("java-library")
    id("chirp-backend.kotlin-common")
}

group = "com.holparb"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jackson.module.kotlin)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}