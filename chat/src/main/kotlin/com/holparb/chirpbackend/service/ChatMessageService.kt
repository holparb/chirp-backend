package com.holparb.chirpbackend.service

import com.holparb.chirpbackend.domain.exception.ChatMessageNotFoundException
import com.holparb.chirpbackend.domain.exception.ChatNotFoundException
import com.holparb.chirpbackend.domain.exception.ChatParticipantNotFoundException
import com.holparb.chirpbackend.domain.exception.ForbiddenException
import com.holparb.chirpbackend.domain.model.ChatMessage
import com.holparb.chirpbackend.domain.type.ChatId
import com.holparb.chirpbackend.domain.type.ChatMessageId
import com.holparb.chirpbackend.domain.type.UserId
import com.holparb.chirpbackend.infra.database.entities.ChatMessageEntity
import com.holparb.chirpbackend.infra.database.mappers.toChatMessage
import com.holparb.chirpbackend.infra.database.repositories.ChatMessageRepository
import com.holparb.chirpbackend.infra.database.repositories.ChatParticipantRepository
import com.holparb.chirpbackend.infra.database.repositories.ChatRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository
) {

    @Transactional
    fun sendMessage(
        chatId: ChatId,
        senderId: UserId,
        content: String,
        messageId: ChatMessageId? = null,
    ): ChatMessage {
        val chat = chatRepository.findChatById(
            id = chatId,
            userId = senderId
        ) ?: throw ChatNotFoundException()

        val sender = chatParticipantRepository.findByIdOrNull(id = senderId)
            ?: throw ChatParticipantNotFoundException(id = senderId)

        return chatMessageRepository.save(
            ChatMessageEntity(
                id = messageId,
                content = content.trim(),
                chatId = chatId,
                chat = chat,
                sender = sender,
            )
        ).toChatMessage()
    }

    @Transactional
    fun deleteMessage(
        messageId: ChatMessageId,
        requestUserId: UserId
    ) {
        val message = chatMessageRepository.findByIdOrNull(messageId)
            ?: throw ChatMessageNotFoundException(messageId)

        if(message.sender.userId != requestUserId) {
            throw ForbiddenException()
        }

        chatMessageRepository.delete(message)
    }
}