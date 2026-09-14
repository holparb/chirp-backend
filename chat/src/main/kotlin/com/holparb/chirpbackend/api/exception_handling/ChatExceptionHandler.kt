package com.holparb.chirpbackend.api.exception_handling

import com.holparb.chirpbackend.domain.exception.ChatMessageNotFoundException
import com.holparb.chirpbackend.domain.exception.ChatNotFoundException
import com.holparb.chirpbackend.domain.exception.ChatParticipantNotFoundException
import com.holparb.chirpbackend.domain.exception.InvalidChatSizeException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

class ChatExceptionHandler {

    @ExceptionHandler(
        ChatNotFoundException::class,
        ChatMessageNotFoundException::class,
        ChatParticipantNotFoundException::class,
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onForbidden(e: Exception) = mapOf(
        "code" to "NOT_FOUND",
        "message" to e.message
    )

    @ExceptionHandler(InvalidChatSizeException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onForbidden(e: InvalidChatSizeException) = mapOf(
        "code" to "INVALID_CHAT_SIZE",
        "message" to e.message
    )
}