package com.holparb.chirpbackend.api.dto

import com.holparb.chirpbackend.api.util.Password
import jakarta.validation.constraints.NotBlank

data class ResetPasswordRequest(
    @field:NotBlank
    val token: String,
    @field:Password
    val newPassword: String
)
