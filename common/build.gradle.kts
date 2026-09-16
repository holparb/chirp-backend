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

    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.amqp)

    implementation(libs.jwt.api)
    runtimeOnly(libs.jwt.impl)
    runtimeOnly(libs.jwt.jackson)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}