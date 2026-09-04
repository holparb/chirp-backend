package com.holparb.chirpbackend.service.auth

import com.holparb.chirpbackend.domain.exception.InvalidTokenException
import com.holparb.chirpbackend.domain.exception.UserNotFoundException
import com.holparb.chirpbackend.domain.model.EmailVerificationToken
import com.holparb.chirpbackend.infra.database.entities.EmailVerificationTokenEntity
import com.holparb.chirpbackend.infra.database.mappers.toEmailVerificationToken
import com.holparb.chirpbackend.infra.database.repositores.EmailVerificationTokenRepository
import com.holparb.chirpbackend.infra.database.repositores.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class EmailVerificationService(
    private val emailVerificationTokenRepository: EmailVerificationTokenRepository,
    private val userRepository: UserRepository,
    @param:Value("\${chirp-backend.email.verification.expiry-hours}") private val expiryHours: Long
) {

    @Transactional
    fun createVerificationToken(email: String): EmailVerificationToken {
        val user = userRepository.findByEmail(email = email) ?: throw UserNotFoundException()
        val existingTokens = emailVerificationTokenRepository.findByUserAndUsedAtIsNull(user = user)

        val now = Instant.now()
        val usedTokens = existingTokens.map { tokenEntity ->
            tokenEntity.apply {
                this.usedAt = now
            }
        }
        emailVerificationTokenRepository.saveAll(usedTokens)

        val emailVerificationToken = EmailVerificationTokenEntity(
            expiresAt = now.plus(expiryHours, ChronoUnit.HOURS),
            user = user,
        )
        return emailVerificationTokenRepository.save(emailVerificationToken).toEmailVerificationToken()
    }

    @Transactional
    fun verifyEmail(emailVerificationToken: String) {
        val verificationToken = emailVerificationTokenRepository.findByToken(emailVerificationToken)
            ?: throw InvalidTokenException("Email verification token is invalid")

        if(verificationToken.used) {
            throw InvalidTokenException("Email verification token is already used")
        }

        if(verificationToken.expired) {
            throw InvalidTokenException("Email verification token has already expired")
        }

        emailVerificationTokenRepository.save(
            verificationToken.apply {
                this.usedAt = Instant.now()
            }
        )

        userRepository.save(
            verificationToken.user.apply {
                emailVerified = true
            }
        )
    }


    @Scheduled(cron = "0 0 3 * * *")
    fun cleanUpExpiredTokens() =
        emailVerificationTokenRepository.deleteByExpiresAtLessThan(now = Instant.now())
}