package com.holparb.chirpbackend.api.dto.ws

import com.holparb.chirpbackend.domain.type.ChatId
import com.holparb.chirpbackend.domain.type.ChatMessageId

data class SendMessageDto(
    val chatId: ChatId,
    val content: String,
    val messageId: ChatMessageId? = null,
)
