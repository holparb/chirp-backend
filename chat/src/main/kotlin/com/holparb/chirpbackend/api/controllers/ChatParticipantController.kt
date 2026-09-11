package com.holparb.chirpbackend.api.controllers

import com.holparb.chirpbackend.api.dto.ChatParticipantDto
import com.holparb.chirpbackend.api.mappers.toChatParticipantDto
import com.holparb.chirpbackend.api.util.requestUserId
import com.holparb.chirpbackend.service.ChatParticipantService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/chat-participants")
class ChatParticipantController(
    private val chatParticipantService: ChatParticipantService
) {

    @GetMapping
    fun getChatParticipantByUsernameOrEmail(
        @RequestParam(required = false) query: String?,

    ): ChatParticipantDto {
        val participant = query?.let {
            chatParticipantService.findChatParticipantByEmailOrUsername(query = query)
        } ?: chatParticipantService.findChatParticipantById(userId = requestUserId)

        return participant?.toChatParticipantDto() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}