package com.holparb.chirpbackend.service.auth

import com.holparb.chirpbackend.domain.exception.InvalidCredentialsException
import com.holparb.chirpbackend.domain.exception.InvalidTokenException
import com.holparb.chirpbackend.domain.exception.SamePasswordException
import com.holparb.chirpbackend.domain.exception.UserNotFoundException
import com.holparb.chirpbackend.domain.model.UserId
import com.holparb.chirpbackend.infra.database.entities.PasswordResetTokenEntity
import com.holparb.chirpbackend.infra.database.repositores.PasswordResetTokenRepository
import com.holparb.chirpbackend.infra.database.repositores.RefreshTokenRepository
import com.holparb.chirpbackend.infra.database.repositores.UserRepository
import com.holparb.chirpbackend.infra.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class PasswordResetService(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    @param:Value("\${chirp-backend.email.reset-password.expiry-minutes}") private val expiryMinutes: Long
) {
    @Transactional
    fun requestPasswordReset(email: String) {
        val user = userRepository.findByEmail(email) ?: return

        passwordResetTokenRepository.invalidateActiveTokensForUser(user = user)

        val passwordResetToken = PasswordResetTokenEntity(
            user = user,
            expiresAt = Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES),
        )
        passwordResetTokenRepository.save(passwordResetToken)

        // TODO: Inform notification service to send email with password reset
    }

    @Transactional
    fun resetPassword(passwordResetToken: String, newPassword: String) {
        val resetToken = passwordResetTokenRepository.findByToken(token = passwordResetToken)
            ?: throw InvalidTokenException("Invalid password reset token")

        if (resetToken.used) {
            throw InvalidTokenException("Password reset token is already used")
        }

        if (resetToken.expired) {
            throw InvalidTokenException("Password reset token has already expired")
        }

        val user = resetToken.user

        if (passwordEncoder.matches(rawPassword = newPassword, hashedPassword = user.hashedPassword)) {
            throw SamePasswordException()
        }
        val hashedNewPassword = passwordEncoder.encode(rawPassword = newPassword)
        userRepository.save(
            user.apply {
                this.hashedPassword = hashedNewPassword
            }
        )

        passwordResetTokenRepository.save(
            resetToken.apply {
                this.usedAt = Instant.now()
            }
        )

        refreshTokenRepository.deleteByUserId(user.id!!)
    }

    @Transactional
    fun changePassword(
        userId: UserId,
        oldPassword: String,
        newPassword: String
    ) {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        if(!passwordEncoder.matches(rawPassword = oldPassword, hashedPassword = user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        if(oldPassword == newPassword) {
            throw SamePasswordException()
        }

        refreshTokenRepository.deleteByUserId(user.id!!)

        val newHashedPassword = passwordEncoder.encode(rawPassword = newPassword)
        userRepository.save(
            user.apply {
                this.hashedPassword = newHashedPassword
            }
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanUpExpiredTokens() =
        passwordResetTokenRepository.deleteByExpiresAtLessThan(Instant.now())
}