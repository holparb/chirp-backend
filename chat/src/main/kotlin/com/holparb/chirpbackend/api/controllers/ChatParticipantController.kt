package com.holparb.chirpbackend.api.controllers

import com.holparb.chirpbackend.api.dto.ChatParticipantDto
import com.holparb.chirpbackend.api.dto.ConfirmProfilePictureRequest
import com.holparb.chirpbackend.api.dto.PictureUploadResponse
import com.holparb.chirpbackend.api.mappers.toChatParticipantDto
import com.holparb.chirpbackend.api.mappers.toResponse
import com.holparb.chirpbackend.api.util.requestUserId
import com.holparb.chirpbackend.service.ChatParticipantService
import com.holparb.chirpbackend.service.ProfilePictureService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/chat-participants")
class ChatParticipantController(
    private val chatParticipantService: ChatParticipantService,
    private val profilePictureService: ProfilePictureService
) {

    @GetMapping
    fun getChatParticipantByUsernameOrEmail(
        @RequestParam(required = false) query: String?,

    ): ChatParticipantDto {
        val participant = query?.let {
            chatParticipantService.findChatParticipantByEmailOrUsername(query = query)
        } ?: chatParticipantService.findChatParticipantById(userId = requestUserId)

        return participant?.toChatParticipantDto() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PostMapping("/profile-picture-upload")
    fun getProfilePictureUploadUrl(
        @RequestParam mimeType: String,
    ): PictureUploadResponse =
        profilePictureService.generateUploadCredentials(
            userId = requestUserId,
            mimeType = mimeType
        ).toResponse()

    @PostMapping("/confirm-profile-picture")
    fun confirmProfilePictureUpload(
        @Valid @RequestBody body: ConfirmProfilePictureRequest,
    ) =
        profilePictureService.confirmProfilePictureUpload(
            userId = requestUserId,
            publicUrl = body.publicUrl,
        )

    @DeleteMapping("/profile-picture")
    fun deleteProfilePicture() =
        profilePictureService.deleteProfilePicture(
            userId = requestUserId,
        )
}