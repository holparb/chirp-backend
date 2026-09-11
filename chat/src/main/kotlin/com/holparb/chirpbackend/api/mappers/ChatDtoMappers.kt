package com.holparb.chirpbackend.api.mappers

import com.holparb.chirpbackend.api.dto.ChatDto
import com.holparb.chirpbackend.api.dto.ChatMessageDto
import com.holparb.chirpbackend.api.dto.ChatParticipantDto
import com.holparb.chirpbackend.domain.model.Chat
import com.holparb.chirpbackend.domain.model.ChatMessage
import com.holparb.chirpbackend.domain.model.ChatParticipant

fun Chat.toChatDto(): ChatDto {
    return ChatDto(
        id = id,
        participants = participants.map {
            it.toChatParticipantDto()
        },
        lastActivityAt = lastActivityAt,
        lastMessage = lastMessage?.toChatMessageDto(),
        creator = creator.toChatParticipantDto()
    )
}

fun ChatMessage.toChatMessageDto(): ChatMessageDto {
    return ChatMessageDto(
        id = id,
        chatId = chatId,
        content = content,
        createdAt = createdAt,
        senderId = sender.userId
    )
}

fun ChatParticipant.toChatParticipantDto(): ChatParticipantDto {
    return ChatParticipantDto(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )
}