package com.holparb.chirpbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChirpBackendApplication

fun main(args: Array<String>) {
    runApplication<ChirpBackendApplication>(*args)
}
