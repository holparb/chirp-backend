package com.holparb.chirpbackend.api.mappers

import com.holparb.chirpbackend.api.dto.AuthenticatedUserDto
import com.holparb.chirpbackend.api.dto.UserDto
import com.holparb.chirpbackend.domain.model.AuthenticatedUser
import com.holparb.chirpbackend.domain.model.User

fun AuthenticatedUser.toAuthenticatedUserDto() = AuthenticatedUserDto(
    user = user.toUserDto(),
    accessToken = accessToken,
    refreshToken = refreshToken,
)

fun User.toUserDto() = UserDto(
    id = id,
    email = email,
    username = username,
    emailVerified = emailVerified
)