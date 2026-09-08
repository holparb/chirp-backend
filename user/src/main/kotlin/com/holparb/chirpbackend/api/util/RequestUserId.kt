package com.holparb.chirpbackend.api.util

import com.holparb.chirpbackend.domain.exception.UnauthorizedException
import com.holparb.chirpbackend.domain.model.UserId
import org.springframework.security.core.context.SecurityContextHolder

val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedException()