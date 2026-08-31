package com.holparb.chirpbackend.infra.database.repositores

import com.holparb.chirpbackend.domain.model.UserId
import com.holparb.chirpbackend.infra.database.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, UserId> {
    fun findByEmail(email: String): UserEntity?
    fun findByEmailAndUsername(email: String, username: String): UserEntity?
}