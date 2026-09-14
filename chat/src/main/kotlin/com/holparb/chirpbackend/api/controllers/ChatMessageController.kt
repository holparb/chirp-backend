package com.holparb.chirpbackend.api.controllers

import com.holparb.chirpbackend.api.util.requestUserId
import com.holparb.chirpbackend.domain.type.ChatMessageId
import com.holparb.chirpbackend.service.ChatMessageService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/messages")
class ChatMessageController(
    private val chatMessageService: ChatMessageService
) {

    @DeleteMapping("/{messageId}")
    fun deleteMessage(
        @PathVariable("messageId") messageId: ChatMessageId,
    ) =
        chatMessageService.deleteMessage(
            messageId = messageId,
            requestUserId = requestUserId
        )
}