package com.holparb.chirpbackend.domain.exception

class InvalidTokenException(
    override val message: String?
): RuntimeException(message ?: "Invalid token")