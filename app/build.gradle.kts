plugins {
    id("chirp-backend.spring-boot-app")
}

group = "com.holparb"
version = "0.0.1-SNAPSHOT"
description = "chirp-backend"

dependencies {
    implementation(projects.chat)
    implementation(projects.user)
    implementation(projects.notification)
    implementation(projects.common)
}
