package com.holparb.chirpbackend.api.controllers

import com.holparb.chirpbackend.api.dto.ChatDto
import com.holparb.chirpbackend.api.dto.CreateChatRequest
import com.holparb.chirpbackend.api.mappers.toChatDto
import com.holparb.chirpbackend.api.util.requestUserId
import com.holparb.chirpbackend.service.ChatService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/chat")
class ChatController(
    private val chatService: ChatService
) {

    fun createChat(
        @Valid @RequestBody body: CreateChatRequest
    ): ChatDto =
        chatService.createChat(
            creatorId = requestUserId,
            otherUserIds = body.otherUserIds.toSet()
        ).toChatDto()
}