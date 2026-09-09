package com.holparb.chirpbackend.domain.model

import com.holparb.chirpbackend.domain.type.UserId

data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val emailVerified: Boolean,
)
