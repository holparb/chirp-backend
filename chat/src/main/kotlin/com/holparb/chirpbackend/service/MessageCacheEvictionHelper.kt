package com.holparb.chirpbackend.service

import com.holparb.chirpbackend.domain.type.ChatId
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component

@Component
class MessageCacheEvictionHelper {

    @CacheEvict(
        cacheNames = ["messages"],
        key = "#chatId",
    )
    fun evictMessagesCache(chatId: ChatId) {
        // NO-OP: Let Spring handle the cache evict
    }
}