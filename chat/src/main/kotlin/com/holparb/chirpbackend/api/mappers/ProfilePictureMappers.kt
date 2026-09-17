package com.holparb.chirpbackend.api.mappers

import com.holparb.chirpbackend.api.dto.PictureUploadResponse
import com.holparb.chirpbackend.domain.model.ProfilePictureUploadCredentials

fun ProfilePictureUploadCredentials.toResponse(): PictureUploadResponse {
    return PictureUploadResponse(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers,
        expiresAt = expiresAt
    )
}