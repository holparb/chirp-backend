package com.holparb.chirpbackend.domain.model

import com.holparb.chirpbackend.domain.type.UserId

data class ChatParticipant(
    val userId: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String? = null,
)
