package com.holparb.chirpbackend.service.mappers

import com.holparb.chirpbackend.domain.model.DeviceToken
import com.holparb.chirpbackend.infra.database.entities.DeviceTokenEntity

fun DeviceTokenEntity.toDeviceToken(): DeviceToken {
    return DeviceToken(
        userId = userId,
        token = token,
        platform = platform.toPlatform(),
        createdAt = createdAt,
        id = id
    )
}