package com.holparb.chirpbackend.api.controllers

import com.holparb.chirpbackend.api.dto.DeviceTokenDto
import com.holparb.chirpbackend.api.dto.RegisterDeviceRequest
import com.holparb.chirpbackend.api.mappers.toDeviceTokenDto
import com.holparb.chirpbackend.api.mappers.toPlatformDto
import com.holparb.chirpbackend.api.util.requestUserId
import com.holparb.chirpbackend.service.pushnotification.PushNotificationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notification")
class DeviceTokenController(
    private val notificationService: PushNotificationService
) {

    @PostMapping("/register")
    fun registerDeviceToken(
        @Valid @RequestBody body: RegisterDeviceRequest
    ): DeviceTokenDto =
        notificationService.registerDevice(
            userId = requestUserId,
            token = body.token,
            platform = body.platform.toPlatformDto()
        ).toDeviceTokenDto()

    @DeleteMapping("/{token}")
    fun unregisterDeviceToken(
        @PathVariable("token") token: String
    ) =
        notificationService.unregisterDevice(token = token)

}