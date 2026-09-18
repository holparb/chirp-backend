package com.holparb.chirpbackend.infra.database.repositories

import com.holparb.chirpbackend.domain.type.UserId
import com.holparb.chirpbackend.infra.database.entities.DeviceTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DeviceTokenRepository: JpaRepository<DeviceTokenEntity, Long> {
    fun findByUserIdIn(userIds: List<UserId>): List<DeviceTokenEntity>
    fun findByToken(token: String): DeviceTokenEntity?
    fun deleteByToken(token: String)
}