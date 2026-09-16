package com.holparb.chirpbackend.api.dto.ws

import com.holparb.chirpbackend.domain.type.ChatId
import com.holparb.chirpbackend.domain.type.ChatMessageId

data class DeleteMessageDto(
    val messageId: ChatMessageId,
    val chatId: ChatId
)
