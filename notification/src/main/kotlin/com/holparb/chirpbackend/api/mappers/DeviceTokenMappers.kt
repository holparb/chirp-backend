package com.holparb.chirpbackend.api.mappers

import com.holparb.chirpbackend.api.dto.DeviceTokenDto
import com.holparb.chirpbackend.api.dto.PlatformDto
import com.holparb.chirpbackend.domain.model.DeviceToken

fun DeviceToken.toDeviceTokenDto(): DeviceTokenDto =
    DeviceTokenDto(
        userId = userId,
        token = token,
        createdAt = createdAt
    )

fun PlatformDto.toPlatformDto(): DeviceToken.Platform {
    return when(this) {
        PlatformDto.ANDROID -> DeviceToken.Platform.ANDROID
        PlatformDto.IOS -> DeviceToken.Platform.IOS
    }
}