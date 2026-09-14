package com.holparb.chirpbackend.domain.event

import com.holparb.chirpbackend.domain.type.ChatId
import com.holparb.chirpbackend.domain.type.UserId

data class ChatParticipantLeftEvent(
    val chatId: ChatId,
    val userId: UserId
)
