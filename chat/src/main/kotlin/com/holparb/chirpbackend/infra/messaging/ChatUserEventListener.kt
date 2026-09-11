package com.holparb.chirpbackend.infra.messaging

import com.holparb.chirpbackend.domain.events.user.UserEvent
import com.holparb.chirpbackend.domain.model.ChatParticipant
import com.holparb.chirpbackend.infra.messagequeue.MessageQueues
import com.holparb.chirpbackend.service.ChatParticipantService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class ChatUserEventListener(
    private val chatParticipantService: ChatParticipantService,
) {

    @RabbitListener(queues = [MessageQueues.CHAT_USER_EVENTS])
    fun handleUserEvent(event: UserEvent) {
        when(event) {
            is UserEvent.Verified -> {
                chatParticipantService.createChatParticipant(
                    chatParticipant = ChatParticipant(
                        userId = event.userId,
                        username = event.username,
                        email = event.email,
                    )
                )
            }
            else -> Unit
        }
    }
}