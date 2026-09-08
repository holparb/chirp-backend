package com.holparb.chirpbackend.api.config

import com.holparb.chirpbackend.service.auth.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
): OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            if(jwtService.validateAccessToken(token = authHeader)) {
                val userId = jwtService.getUserIdFromToken(token = authHeader)
                val auth = UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    emptyList()
                )
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        filterChain.doFilter(request, response)
    }
}