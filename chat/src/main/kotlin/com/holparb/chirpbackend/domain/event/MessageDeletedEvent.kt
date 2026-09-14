package com.holparb.chirpbackend.domain.event

import com.holparb.chirpbackend.domain.type.ChatId
import com.holparb.chirpbackend.domain.type.ChatMessageId

data class MessageDeletedEvent(
    val messageId: ChatMessageId,
    val chatId: ChatId,
)
