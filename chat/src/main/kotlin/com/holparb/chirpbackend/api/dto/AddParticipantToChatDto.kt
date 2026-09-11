package com.holparb.chirpbackend.api.dto

import com.holparb.chirpbackend.domain.type.UserId
import jakarta.validation.constraints.Size

data class AddParticipantToChatDto(
    @field:Size(min = 1, message = "Must be at least one participant")
    val userIds: List<UserId>
)