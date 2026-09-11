package com.holparb.chirpbackend.service

import com.holparb.chirpbackend.domain.exception.ChatNotFoundException
import com.holparb.chirpbackend.domain.exception.ChatParticipantNotFoundException
import com.holparb.chirpbackend.domain.exception.ForbiddenException
import com.holparb.chirpbackend.domain.exception.InvalidChatSizeException
import com.holparb.chirpbackend.domain.model.Chat
import com.holparb.chirpbackend.domain.model.ChatMessage
import com.holparb.chirpbackend.domain.type.ChatId
import com.holparb.chirpbackend.domain.type.UserId
import com.holparb.chirpbackend.infra.database.entities.ChatEntity
import com.holparb.chirpbackend.infra.database.mappers.toChat
import com.holparb.chirpbackend.infra.database.mappers.toChatMessage
import com.holparb.chirpbackend.infra.database.repositories.ChatMessageRepository
import com.holparb.chirpbackend.infra.database.repositories.ChatParticipantRepository
import com.holparb.chirpbackend.infra.database.repositories.ChatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository
) {

    @Transactional
    fun createChat(
        creatorId: UserId,
        otherUserIds: Set<UserId>,
    ): Chat {
        val otherParticipants = chatParticipantRepository.findByUserIdIn(userIds = otherUserIds)

        val allParticipants = (otherParticipants + creatorId)
        if (allParticipants.size < 2) {
            throw InvalidChatSizeException()
        }

        val creator = chatParticipantRepository.findByIdOrNull(creatorId)
            ?: throw ChatParticipantNotFoundException(id = creatorId)

        return chatRepository.save(
            ChatEntity(
                creator = creator,
                participants = setOf(creator) + otherParticipants
            )
        ).toChat(lastMessage = null)
    }

    fun addParticipants(
        requestUserId: UserId,
        chatId: ChatId,
        userIds: Set<UserId>
    ): Chat {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()

        val requestingUserInChat = chat.participants.any {
            it.userId == requestUserId
        }
        if(!requestingUserInChat) {
            throw ForbiddenException()
        }

        val users = userIds.map { userId ->
            chatParticipantRepository.findByIdOrNull(id = userId)
                ?: throw ChatParticipantNotFoundException(id = userId)
        }

        return chatRepository.save(
            chat.apply {
                this.participants = chat.participants + users
            }
        ).toChat(lastMessage = lastMessageForChat(chatId = chatId))
    }

    fun removeParticipant(chatId: ChatId, userId: UserId) {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()

        val participant = chat.participants.find { it.userId == userId }
            ?: throw ChatParticipantNotFoundException(id = chatId)

        val newParticipantsSize = chat.participants.size - 1
        if(newParticipantsSize == 0) {
            chatRepository.deleteById(chatId)
            return
        }

        chatRepository.save(
            chat.apply {
                this.participants = chat.participants - participant
            }
        )
    }

    private fun lastMessageForChat(chatId: ChatId): ChatMessage? =
        chatMessageRepository.findLatestMessagesByChatIds(setOf(chatId))
            .firstOrNull()
            ?.toChatMessage()
}