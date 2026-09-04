package com.holparb.chirpbackend.infra.database.mappers

import com.holparb.chirpbackend.domain.model.EmailVerificationToken
import com.holparb.chirpbackend.infra.database.entities.EmailVerificationTokenEntity

fun EmailVerificationTokenEntity.toEmailVerificationToken(): EmailVerificationToken =
    EmailVerificationToken(
        id = id,
        token = token,
        user = user.toUser()
    )