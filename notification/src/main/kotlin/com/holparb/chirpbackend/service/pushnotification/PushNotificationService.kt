package com.holparb.chirpbackend.service.pushnotification

import com.holparb.chirpbackend.domain.exception.InvalidDeviceTokenException
import com.holparb.chirpbackend.domain.model.DeviceToken
import com.holparb.chirpbackend.domain.model.PushNotification
import com.holparb.chirpbackend.domain.type.ChatId
import com.holparb.chirpbackend.domain.type.UserId
import com.holparb.chirpbackend.infra.database.entities.DeviceTokenEntity
import com.holparb.chirpbackend.infra.database.repositories.DeviceTokenRepository
import com.holparb.chirpbackend.infra.pushnotification.FirebasePushNotificationService
import com.holparb.chirpbackend.service.mappers.toDeviceToken
import com.holparb.chirpbackend.service.mappers.toPlatformEntity
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentSkipListMap

@Service
class PushNotificationService(
    private val deviceTokenRepository: DeviceTokenRepository,
    private val firebasePushNotificationService: FirebasePushNotificationService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val retryQueue = ConcurrentSkipListMap<Long, MutableList<RetryData>>()

    @Transactional
    fun registerDevice(
        userId: UserId,
        token: String,
        platform: DeviceToken.Platform
    ): DeviceToken {
        val existing = deviceTokenRepository.findByToken(token = token)
        val trimmedToken = token.trim()

        if(existing == null && !firebasePushNotificationService.tokenValid(trimmedToken)) {
            throw InvalidDeviceTokenException()
        }

        val entity = if(existing != null) {
            deviceTokenRepository.save(
                existing.apply {
                    this.userId = userId
                }
            )
        } else {
            deviceTokenRepository.save(
                DeviceTokenEntity(
                    token = trimmedToken,
                    userId = userId,
                    platform = platform.toPlatformEntity()
                )
            )
        }
        return entity.toDeviceToken()
    }

    @Transactional
    fun unregisterDevice(token: String) = deviceTokenRepository.deleteByToken(token = token)

    fun sendNewMessageNotifications(
        recipientUserIds: List<UserId>,
        senderUserId: UserId,
        senderUsername: String,
        message: String,
        chatId: ChatId
    ) {
        val deviceTokens = deviceTokenRepository.findByUserIdIn(userIds = recipientUserIds)
        if(deviceTokens.isEmpty()) {
            logger.info("No device tokens found")
            return
        }

        val recipients = deviceTokens
            .filter { it.userId != senderUserId }
            .map { it.toDeviceToken() }

        val notification = PushNotification(
            title = "New message from $senderUsername",
            message = message,
            recipients = recipients,
            chatId = chatId,
            data = mapOf(
                "chatId" to chatId.toString(),
                "type" to "new_message",
            )
        )

        sendWithRetry(notification = notification)
    }

    fun sendWithRetry(
        notification: PushNotification,
        attempt: Int = 0,
    ) {
        val result = firebasePushNotificationService.sendNotification(notification = notification)

        result.permanentFailures.forEach { deviceToken ->
            deviceTokenRepository.deleteByToken(token = deviceToken.token)
        }

        if(result.temporaryFailures.isNotEmpty() && attempt <= RETRY_DELAYS_SECONDS.size) {
            val retryNotification = notification.copy(
                recipients = result.temporaryFailures,
            )

            scheduleRetry(
                notification = retryNotification,
                attempt = attempt + 1,
            )
        }

        if(result.succeeded.isNotEmpty()) {
            logger.info("Successfully sent notification to ${result.succeeded.size} devices")
        }
    }

    private fun scheduleRetry(
        notification: PushNotification,
        attempt: Int,
    ) {
        val delay = RETRY_DELAYS_SECONDS.getOrElse(attempt - 1) { RETRY_DELAYS_SECONDS.last() }
        val executeAtMs = Instant.now().plusSeconds(delay).toEpochMilli()

        val retryData = RetryData(
            notification = notification,
            attempt = attempt,
            createdAt = Instant.now(),
        )

        retryQueue.compute(executeAtMs) { _, retries ->
            (retries ?: mutableListOf()).apply { add(retryData) }
        }
        logger.info("Scheduled retry $attempt for ${notification.id} in $delay seconds")
    }

    @Scheduled(fixedDelay = 15_000L)
    fun processRetries() {
        val now = Instant.now()
        val nowMillis = now.toEpochMilli()

        val toProcess = retryQueue.headMap(nowMillis, true)

        if(toProcess.isEmpty()) {
            return
        }

        val entries = toProcess.entries.toList()
        entries.forEach { (timeMillis, retries) ->
            retryQueue.remove(timeMillis)

            retries.forEach { retry ->
                try {
                    val age = Duration.between(retry.createdAt, now)
                    if(age.toMinutes() > MAX_RETRY_AGE_MINUTES) {
                        logger.warn("Dropping old retry (${age.toMinutes()} old)")
                        return@forEach
                    }

                    sendWithRetry(
                        notification = retry.notification,
                        attempt = retry.attempt
                    )
                } catch(e: Exception) {
                    logger.warn("Error processing retry ${retry.notification.id}", e)
                }
            }
        }
    }

    private data class RetryData(
        val notification: PushNotification,
        val attempt: Int,
        val createdAt: Instant
    )

    private companion object {
        val RETRY_DELAYS_SECONDS = listOf(
            30L,
            60L,
            120L,
            300L,
            600L
        )
        const val MAX_RETRY_AGE_MINUTES = 30L
    }
}