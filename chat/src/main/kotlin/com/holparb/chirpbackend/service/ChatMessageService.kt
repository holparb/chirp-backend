package com.holparb.chirpbackend.service

import com.holparb.chirpbackend.domain.event.MessageDeletedEvent
import com.holparb.chirpbackend.domain.events.chat.ChatEvent
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
import com.holparb.chirpbackend.infra.messagequeue.EventPublisher
import jakarta.transaction.Transactional
import org.springframework.cache.annotation.CacheEvict
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val eventPublisher: EventPublisher,
    private val messageCacheEvictionHelper: MessageCacheEvictionHelper
) {

    @Transactional
    @CacheEvict(
        cacheNames = ["messages"],
        key = "#chatId",
    )
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

        val savedMessage = chatMessageRepository.saveAndFlush(
            ChatMessageEntity(
                id = messageId ?: UUID.randomUUID(),
                content = content.trim(),
                chatId = chatId,
                chat = chat,
                sender = sender,
            )
        )

        eventPublisher.publish(
            event = ChatEvent.NewMessage(
                senderId = senderId,
                chatId = chatId,
                senderUsername = sender.username,
                message = content,
                recipientIds = chat.participants.map { it.userId }.toSet(),
            )
        )

        return savedMessage.toChatMessage()
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

        applicationEventPublisher.publishEvent(
            MessageDeletedEvent(
                messageId = messageId,
                chatId = message.chatId,
            )
        )

        messageCacheEvictionHelper.evictMessagesCache(chatId = message.chatId)
    }
}