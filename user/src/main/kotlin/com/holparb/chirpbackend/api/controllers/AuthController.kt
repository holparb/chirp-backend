package com.holparb.chirpbackend.api.controllers

import com.holparb.chirpbackend.api.dto.AuthenticatedUserDto
import com.holparb.chirpbackend.api.dto.LoginRequest
import com.holparb.chirpbackend.api.dto.RefreshRequest
import com.holparb.chirpbackend.api.dto.RegistrationRequest
import com.holparb.chirpbackend.api.dto.UserDto
import com.holparb.chirpbackend.api.mappers.toAuthenticatedUserDto
import com.holparb.chirpbackend.api.mappers.toUserDto
import com.holparb.chirpbackend.service.auth.AuthService
import com.holparb.chirpbackend.service.auth.EmailVerificationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody body: RegistrationRequest
    ): UserDto =
        authService.registerUser(
            email = body.email,
            username = body.username,
            password = body.password,
        ).toUserDto()

    @PostMapping("/login")
    fun logIn(
        @RequestBody body: LoginRequest
    ): AuthenticatedUserDto =
        authService.logIn(
            email = body.email,
            password = body.password
        ).toAuthenticatedUserDto()

    @PostMapping("/refresh")
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
        @RequestParam emailVerificationToken: String
    ) = emailVerificationService.verifyEmail(emailVerificationToken = emailVerificationToken)
}