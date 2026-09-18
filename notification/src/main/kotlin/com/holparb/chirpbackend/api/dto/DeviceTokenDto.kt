package com.holparb.chirpbackend.api.dto

import com.holparb.chirpbackend.domain.type.UserId
import java.time.Instant

data class DeviceTokenDto(
    val userId: UserId,
    val token: String,
    val createdAt: Instant
)
