package com.holparb.chirpbackend.api.config


import com.holparb.chirpbackend.domain.exception.RateLimitException
import com.holparb.chirpbackend.infra.ratelimiting.IpRateLmiter
import com.holparb.chirpbackend.infra.ratelimiting.IpResolver
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Duration

@Component
class IpRateLimitInterceptor(
    private val ipRateLmiter: IpRateLmiter,
    private val ipResolver: IpResolver,
    @param:Value("\${chirp-backend.rate-limit.ip.apply-limit}") private val applyLimit: Boolean,
): HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if(handler is HandlerMethod && applyLimit) {
            val annotation = handler.getMethodAnnotation(IpRateLimit::class.java)
            if(annotation != null) {
                val clientIp = ipResolver.getClientIp(request = request)

                return try {
                    ipRateLmiter.withIpRateLimit(
                        ipAddress = clientIp,
                        resetsIn = Duration.of(annotation.duration, annotation.unit.toChronoUnit()),
                        maxRequestsPerIp = annotation.requests,
                        action = { true }
                    )
                } catch(e: RateLimitException) {
                    response.sendError(429)
                    false
                }
            }
        }
        return super.preHandle(request, response, handler)
    }
}