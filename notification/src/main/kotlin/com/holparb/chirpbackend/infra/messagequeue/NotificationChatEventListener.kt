package com.holparb.chirpbackend.infra.messagequeue

import com.holparb.chirpbackend.domain.events.chat.ChatEvent
import com.holparb.chirpbackend.service.pushnotification.PushNotificationService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class NotificationChatEventListener(
    private val pushNotificationService: PushNotificationService
) {

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_CHAT_EVENTS])
    fun handleChatEvent(event: ChatEvent) {
        when(event) {
            is ChatEvent.NewMessage -> pushNotificationService.sendNewMessageNotifications(
                recipientUserIds = event.recipientIds.toList(),
                senderUsername = event.senderUsername,
                senderUserId = event.senderId,
                message = event.message,
                chatId = event.chatId
            )
        }
    }
}