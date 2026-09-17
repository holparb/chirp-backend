package com.holparb.chirpbackend.domain.event

import com.holparb.chirpbackend.domain.type.UserId

data class ProfilePictureUpdatedEvent(
    val userId: UserId,
    val newUrl: String?
)
