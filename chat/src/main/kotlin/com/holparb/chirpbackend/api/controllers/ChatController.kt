package com.holparb.chirpbackend.api.controllers

import com.holparb.chirpbackend.api.dto.AddParticipantToChatDto
import com.holparb.chirpbackend.api.dto.ChatDto
import com.holparb.chirpbackend.api.dto.ChatMessageDto
import com.holparb.chirpbackend.api.dto.CreateChatRequest
import com.holparb.chirpbackend.api.mappers.toChatDto
import com.holparb.chirpbackend.api.util.requestUserId
import com.holparb.chirpbackend.domain.type.ChatId
import com.holparb.chirpbackend.service.ChatService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/v1/chat")
class ChatController(
    private val chatService: ChatService
) {

    @GetMapping("/{chatId}/messages")
    fun getMessagesForChat(
        @PathVariable("chatId") chatId: ChatId,
        @RequestParam("before", required = false) before: Instant? = null,
        @RequestParam("pageSize", required = false) pageSize: Int = DEFAULT_PAGE_SIZE
    ): List<ChatMessageDto> =
        chatService.getChatMessages(
            chatId = chatId,
            before = before,
            pageSize = pageSize
        )

    @PostMapping
    fun createChat(
        @Valid @RequestBody body: CreateChatRequest
    ): ChatDto =
        chatService.createChat(
            creatorId = requestUserId,
            otherUserIds = body.otherUserIds.toSet()
        ).toChatDto()

    @PostMapping("/{chatId}/add")
    fun addChatParticipants(
        @PathVariable chatId: ChatId,
        @Valid @RequestBody body: AddParticipantToChatDto
    ): ChatDto =
        chatService.addParticipants(
            requestUserId = requestUserId,
            chatId = chatId,
            userIds = body.userIds.toSet()
        ).toChatDto()

    @DeleteMapping("/{chatId}/leave")
    fun leaveChat(
        @PathVariable chatId: ChatId,
    ) =
        chatService.removeParticipant(
            chatId = chatId,
            userId = requestUserId
        )

    private companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}