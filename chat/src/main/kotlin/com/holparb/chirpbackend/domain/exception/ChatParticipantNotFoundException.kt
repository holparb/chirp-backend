package com.holparb.chirpbackend.domain.exception

import com.holparb.chirpbackend.domain.type.UserId

class ChatParticipantNotFoundException(
    private val id: UserId
): RuntimeException(
    "The chat participant with the ID $id was not found."
)