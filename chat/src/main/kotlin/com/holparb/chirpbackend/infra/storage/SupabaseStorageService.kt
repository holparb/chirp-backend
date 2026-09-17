package com.holparb.chirpbackend.infra.storage

import com.holparb.chirpbackend.domain.exception.InvalidProfilePictureException
import com.holparb.chirpbackend.domain.exception.StorageException
import com.holparb.chirpbackend.domain.model.ProfilePictureUploadCredentials
import com.holparb.chirpbackend.domain.type.UserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.time.Instant
import java.util.*

@Service
class SupabaseStorageService(
    private val supabaseRestClient: RestClient,
    @param:Value("\${supabase.url}") private val supabaseUrl: String,
) {

    fun generateSignedUploadUrl(userId: UserId, mimeType: String): ProfilePictureUploadCredentials {
        val extension = allowedMimeTypes[mimeType]
            ?: throw InvalidProfilePictureException("Invalid mime type $mimeType")

        val fileName = "user_${userId}_${UUID.randomUUID()}.$extension"
        val path = "profile-pictures/$userId/$fileName.$extension"

        val publicUrl = "$supabaseUrl/storage/v1/object/public/$path"

        return ProfilePictureUploadCredentials(
            uploadUrl = createSignedUrl(
                path = path,
                expiresInSeconds = 300
            ),
            publicUrl = publicUrl,
            headers = mapOf(
                "Content-Type" to mimeType
            ),
            expiresAt = Instant.now().plusSeconds(300)
        )
    }

    fun deleteFile(url: String) {
        val path = if(url.contains("/object/public/")) {
            url.substringAfter("/object/public/")
        }
        else throw StorageException("Invalid URL format")

        val deleteUrl = "/storage/v1/object/$path"

        val response = supabaseRestClient
            .delete()
            .uri(deleteUrl)
            .retrieve()
            .toBodilessEntity()

        if(response.statusCode.isError) {
            throw StorageException("Unable to delete file: ${response.statusCode}")
        }
    }

    private fun createSignedUrl(
        path: String,
        expiresInSeconds: Int
    ): String {
        val json = """
            { "expiresIn": $expiresInSeconds }
        """.trimIndent()

        val response = supabaseRestClient
            .post()
            .uri("/storage/v1/object/upload/sign/$path")
            .body(json)
            .retrieve()
            .body(SignedUploadResponse::class.java)
            ?: throw StorageException("Failed to create signed URL")

        return "$supabaseUrl/storage/v1${response.url}"
    }

    private data class SignedUploadResponse(
        val url: String,
    )

    private companion object {
        val allowedMimeTypes = mapOf(
            "image/jpeg" to "jpg",
            "image/jpg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp",
        )
    }
}