package com.holparb.chirpbackend.domain.exception

class StorageException(
    override val message: String? = null
): RuntimeException(message ?: "Storage error")