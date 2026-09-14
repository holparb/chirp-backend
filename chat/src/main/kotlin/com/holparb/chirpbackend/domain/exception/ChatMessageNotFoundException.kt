package com.holparb.chirpbackend.domain.exception

import com.holparb.chirpbackend.domain.type.ChatMessageId

class ChatMessageNotFoundException(
    private val messageId: ChatMessageId,
): RuntimeException("Message with $messageId could not be found.")