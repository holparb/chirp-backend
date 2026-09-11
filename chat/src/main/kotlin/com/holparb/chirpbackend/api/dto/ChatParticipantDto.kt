package com.holparb.chirpbackend.api.dto

import com.holparb.chirpbackend.domain.type.UserId

data class ChatParticipantDto(
    val userId: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String? = null
)
