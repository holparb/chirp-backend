package com.holparb.chirpbackend.infra.database.mappers

import com.holparb.chirpbackend.domain.model.User
import com.holparb.chirpbackend.infra.database.entities.UserEntity

fun UserEntity.toUser(): User =
    User(
        id = id!!,
        username = username,
        email = email,
        emailVerified = emailVerified
    )