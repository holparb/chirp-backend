package com.holparb.chirpbackend.api.config

import java.util.concurrent.TimeUnit

annotation class IpRateLimit(
    val requests: Int = 30,
    val duration: Long = 1L,
    val unit: TimeUnit = TimeUnit.MINUTES,
)
