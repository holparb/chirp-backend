package com.holparb.chirpbackend.api.dto.ws

import com.holparb.chirpbackend.domain.type.UserId

data class ProfilePictureUpdateDto(
    val userId: UserId,
    val newUrl: String? = null
)
