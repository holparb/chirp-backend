package com.holparb.chirpbackend.api.dto

data class AuthenticatedUserDto(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String,
)
