package com.holparb.chirpbackend.domain.exception

class RateLimitException(
    val resetsInSeconds: Long,
): RuntimeException("Rate limit exceeded, try again in $resetsInSeconds seconds")