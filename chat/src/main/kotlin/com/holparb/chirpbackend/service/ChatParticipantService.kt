package com.holparb.chirpbackend.service

import com.holparb.chirpbackend.domain.model.ChatParticipant
import com.holparb.chirpbackend.domain.type.UserId
import com.holparb.chirpbackend.infra.database.mappers.toChatParticipant
import com.holparb.chirpbackend.infra.database.mappers.toChatParticipantEntity
import com.holparb.chirpbackend.infra.database.repositories.ChatParticipantRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatParticipantService(
    private val chatParticipantRepository: ChatParticipantRepository
) {

    fun createChatParticipant(chatParticipant: ChatParticipant) =
        chatParticipantRepository.save(chatParticipant.toChatParticipantEntity())

    fun findChatParticipantById(userId: UserId): ChatParticipant? =
        chatParticipantRepository.findByIdOrNull(id = userId)?.toChatParticipant()

    fun findChatParticipantByEmailOrUsername(query: String): ChatParticipant? =
        chatParticipantRepository.findByEmailOrUsername(query = query.lowercase().trim())?.toChatParticipant()
}