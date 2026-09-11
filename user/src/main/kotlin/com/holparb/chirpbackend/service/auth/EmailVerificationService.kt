package com.holparb.chirpbackend.service.auth

import com.holparb.chirpbackend.domain.events.user.UserEvent
import com.holparb.chirpbackend.domain.exception.InvalidTokenException
import com.holparb.chirpbackend.domain.exception.UserNotFoundException
import com.holparb.chirpbackend.domain.model.EmailVerificationToken
import com.holparb.chirpbackend.infra.database.entities.EmailVerificationTokenEntity
import com.holparb.chirpbackend.infra.database.mappers.toEmailVerificationToken
import com.holparb.chirpbackend.infra.database.repositores.EmailVerificationTokenRepository
import com.holparb.chirpbackend.infra.database.repositores.UserRepository
import com.holparb.chirpbackend.infra.messagequeue.EventPublisher
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
    private val eventPublisher: EventPublisher,
    @param:Value("\${chirp-backend.email.verification.expiry-hours}")
    private val expiryHours: Long
) {

    @Transactional
    fun resendVerificationEmail(email: String) {
        val emailVerificationToken = createVerificationToken(email = email)

        if(emailVerificationToken.user.emailVerified) return

        eventPublisher.publish(
            event = UserEvent.RequestResendVerification(
                userId = emailVerificationToken.user.id,
                username = emailVerificationToken.user.username,
                email = email,
                verificationToken = emailVerificationToken.token,
            )
        )
    }

    @Transactional
    fun createVerificationToken(email: String): EmailVerificationToken {
        val user = userRepository.findByEmail(email = email) ?: throw UserNotFoundException()

        emailVerificationTokenRepository.invalidateActiveTokensForUser(user = user)

        val emailVerificationToken = EmailVerificationTokenEntity(
            expiresAt = Instant.now().plus(expiryHours, ChronoUnit.HOURS),
            user = user,
        )
        return emailVerificationTokenRepository.save(emailVerificationToken).toEmailVerificationToken()
    }

    @Transactional
    fun verifyEmail(token: String) {
        val verificationToken = emailVerificationTokenRepository.findByToken(token)
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

        eventPublisher.publish(
            event = UserEvent.Verified(
                userId = verificationToken.user.id!!,
                username = verificationToken.user.username,
                email = verificationToken.user.email,
            )
        )
    }


    @Scheduled(cron = "0 0 3 * * *")
    fun cleanUpExpiredTokens() =
        emailVerificationTokenRepository.deleteByExpiresAtLessThan(now = Instant.now())
}