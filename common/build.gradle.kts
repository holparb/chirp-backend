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
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}