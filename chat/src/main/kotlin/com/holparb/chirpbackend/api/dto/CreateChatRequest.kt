package com.holparb.chirpbackend.api.dto

import com.holparb.chirpbackend.domain.type.UserId
import jakarta.validation.constraints.Size

data class CreateChatRequest(
    @field:Size(min = 1, message = "Chats must have at least 2 unique participants")
    val otherUserIds: Set<UserId>,
)
