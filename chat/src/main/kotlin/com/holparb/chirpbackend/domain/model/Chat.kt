package com.holparb.chirpbackend.domain.model

import com.holparb.chirpbackend.domain.type.ChatId
import java.time.Instant

data class Chat(
    val id: ChatId,
    val participants: Set<ChatParticipant>,
    val lastMessage: ChatMessage? = null,
    val creator: ChatParticipant,
    val lastActivityAt: Instant,
    val createdAt: Instant,
)
