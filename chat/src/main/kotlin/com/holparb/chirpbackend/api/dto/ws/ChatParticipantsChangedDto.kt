package com.holparb.chirpbackend.api.dto.ws

import com.holparb.chirpbackend.domain.type.ChatId

data class ChatParticipantsChangedDto (
    val chatId: ChatId,
)