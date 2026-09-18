import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("chirp-backend.spring-boot-app")
}

group = "com.holparb"
version = "0.0.1-SNAPSHOT"

tasks {
    named<BootJar>("bootJar") {
        from(project(":notification").projectDir.resolve("src/main/resources")) {
            into("")
        }
        from(project(":user").projectDir.resolve("src/main/resources")) {
            into("")
        }
    }
}

dependencies {
    implementation(projects.chat)
    implementation(projects.user)
    implementation(projects.notification)
    implementation(projects.common)

    implementation(libs.spring.boot.starter.security)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.mail)
    runtimeOnly(libs.postgresql)
}
