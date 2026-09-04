package com.holparb.chirpbackend.service.auth

import com.holparb.chirpbackend.domain.exception.InvalidCredentialsException
import com.holparb.chirpbackend.domain.exception.InvalidTokenException
import com.holparb.chirpbackend.domain.exception.UserAlreadyExistsException
import com.holparb.chirpbackend.domain.exception.UserNotFoundException
import com.holparb.chirpbackend.domain.model.AuthenticatedUser
import com.holparb.chirpbackend.domain.model.User
import com.holparb.chirpbackend.domain.model.UserId
import com.holparb.chirpbackend.infra.database.entities.RefreshTokenEntity
import com.holparb.chirpbackend.infra.database.entities.UserEntity
import com.holparb.chirpbackend.infra.database.mappers.toUser
import com.holparb.chirpbackend.infra.database.repositores.RefreshTokenRepository
import com.holparb.chirpbackend.infra.database.repositores.UserRepository
import com.holparb.chirpbackend.infra.security.PasswordEncoder
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun registerUser(email: String, username: String, password: String): User {
        val user = userRepository.findByEmailAndUsername(
            email = email.trim(),
            username = username.trim()
        )
        if (user != null) throw UserAlreadyExistsException()

        return userRepository.save(
            UserEntity(
                email = email.trim(),
                username = username.trim(),
                hashedPassword = passwordEncoder.encode(rawPassword = password)
            )
        ).toUser()
    }

    fun logIn(
        email: String,
        password: String
    ): AuthenticatedUser {
        val user = userRepository.findByEmail(email.trim()) ?: throw InvalidCredentialsException()

        if (!passwordEncoder.matches(rawPassword = password, hashedPassword = user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        // TODO: check for verified email

        return user.id?.let { userId ->
            val accessToken = jwtService.generateAccessToken(userId = userId)
            val refreshToken = jwtService.generateRefreshToken(userId = userId)

            storeRefreshToken(userId = userId, token = refreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserNotFoundException()

    }

    @Transactional
    fun refresh(refreshToken: String): AuthenticatedUser {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw InvalidTokenException(message = "Invalid refresh token")
        }

        val userId = jwtService.getUserIdFromToken(token = refreshToken)
        val user = userRepository.findByIdOrNull(id = userId) ?: throw UserNotFoundException()

        val hashedToken = hashToken(token = refreshToken)

        return user.id?.let { userId ->
            refreshTokenRepository.findByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashedToken
            ) ?: InvalidTokenException(message = "Invalid refresh token")

            refreshTokenRepository.deleteByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashedToken
            )

            val accessToken = jwtService.generateAccessToken(userId = userId)
            val refreshToken = jwtService.generateRefreshToken(userId = userId)

            storeRefreshToken(userId = userId, token = refreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun logOut(refreshToken: String) {
        val userId = jwtService.getUserIdFromToken(token = refreshToken)
        val hashedToken = hashToken(token = refreshToken)
        refreshTokenRepository.deleteByUserIdAndHashedToken(
            userId = userId,
            hashedToken = hashedToken
        )
    }

    private fun storeRefreshToken(userId: UserId, token: String) {
        val hashedToken = hashToken(token = token)
        val expiryInMs = jwtService.refreshTokenValidityInMs
        val expiresAt = Instant.now().plusMillis(expiryInMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashedToken
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}