package com.holparb.chirpbackend.infra.database.mappers

import com.holparb.chirpbackend.domain.model.Chat
import com.holparb.chirpbackend.domain.model.ChatMessage
import com.holparb.chirpbackend.domain.model.ChatParticipant
import com.holparb.chirpbackend.infra.database.entities.ChatEntity
import com.holparb.chirpbackend.infra.database.entities.ChatMessageEntity
import com.holparb.chirpbackend.infra.database.entities.ChatParticipantEntity

fun ChatEntity.toChat(lastMessage: ChatMessage? = null): Chat =
    Chat(
        id = id!!,
        participants = participants.map { it.toChatParticipant() }.toSet(),
        creator = creator.toChatParticipant(),
        lastMessage = lastMessage,
        lastActivityAt = lastMessage?.createdAt ?: createdAt,
        createdAt = createdAt,
    )

fun ChatParticipantEntity.toChatParticipant(): ChatParticipant =
    ChatParticipant(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )

fun ChatParticipant.toChatParticipantEntity(): ChatParticipantEntity =
    ChatParticipantEntity(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )

fun ChatMessageEntity.toChatMessage(): ChatMessage =
    ChatMessage(
        id = id!!,
        chatId = chatId,
        content = content,
        sender = sender.toChatParticipant(),
        createdAt = createdAt,
    )