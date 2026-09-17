package com.holparb.chirpbackend.domain.exception

class InvalidProfilePictureException(
    override val message: String? = null
): RuntimeException(message ?: "Invalid profile picture data")