package com.holparb.chirpbackend.api.controllers

import com.holparb.chirpbackend.api.config.IpRateLimit
import com.holparb.chirpbackend.api.dto.AuthenticatedUserDto
import com.holparb.chirpbackend.api.dto.ChangePasswordRequest
import com.holparb.chirpbackend.api.dto.EmailRequest
import com.holparb.chirpbackend.api.dto.LoginRequest
import com.holparb.chirpbackend.api.dto.RefreshRequest
import com.holparb.chirpbackend.api.dto.RegistrationRequest
import com.holparb.chirpbackend.api.dto.ResetPasswordRequest
import com.holparb.chirpbackend.api.dto.UserDto
import com.holparb.chirpbackend.api.mappers.toAuthenticatedUserDto
import com.holparb.chirpbackend.api.mappers.toUserDto
import com.holparb.chirpbackend.api.util.requestUserId
import com.holparb.chirpbackend.infra.ratelimiting.EmailRateLimiter
import com.holparb.chirpbackend.service.auth.AuthService
import com.holparb.chirpbackend.service.auth.EmailVerificationService
import com.holparb.chirpbackend.service.auth.PasswordResetService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val passwordResetService: PasswordResetService,
    private val emailRateLimiter: EmailRateLimiter
) {

    @PostMapping("/register")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun register(
        @Valid @RequestBody body: RegistrationRequest
    ): UserDto =
        authService.registerUser(
            email = body.email,
            username = body.username,
            password = body.password,
        ).toUserDto()

    @PostMapping("/login")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun logIn(
        @RequestBody body: LoginRequest
    ): AuthenticatedUserDto =
        authService.logIn(
            email = body.email,
            password = body.password
        ).toAuthenticatedUserDto()

    @PostMapping("/refresh")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun refresh(
        @RequestBody body: RefreshRequest
    ): AuthenticatedUserDto =
        authService.refresh(refreshToken = body.refreshToken).toAuthenticatedUserDto()

    @PostMapping("/logout")
    fun logOut(
        @RequestBody body: RefreshRequest
    ) = authService.logOut(refreshToken = body.refreshToken)

    @GetMapping("/verify")
    fun verifyEmail(
        @RequestParam token: String
    ) = emailVerificationService.verifyEmail(token = token)

    @PostMapping("/forgot-password")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun forgotPassword(
        @RequestBody body: EmailRequest
    ) = passwordResetService.requestPasswordReset(email = body.email)

    @PostMapping("/reset-password")
    fun resetPassword(
        @Valid @RequestBody body: ResetPasswordRequest
    ) =
        passwordResetService.resetPassword(
            passwordResetToken = body.token,
            newPassword = body.newPassword
        )

    @PostMapping("/change-password")
    fun changePassword(
        @Valid @RequestBody body: ChangePasswordRequest
    ) =
        passwordResetService.changePassword(
            userId = requestUserId,
            oldPassword = body.newPassword,
            newPassword = body.newPassword
        )

    @PostMapping("/resend-verification")
    fun resendVerification(
        @Valid @RequestBody body: EmailRequest
    ) =
        emailRateLimiter.withRateLimit(
            email = body.email
        ) {
            emailVerificationService.resendVerificationEmail(email = body.email)
        }
}