package com.holparb.chirpbackend.api.dto

import com.holparb.chirpbackend.domain.model.UserId

data class UserDto(
    val id: UserId,
    val email: String,
    val username: String,
    val emailVerified: Boolean,
)
