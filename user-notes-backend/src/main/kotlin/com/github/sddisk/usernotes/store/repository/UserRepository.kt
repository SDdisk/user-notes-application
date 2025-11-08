package com.github.sddisk.usernotes.store.repository

import com.github.sddisk.usernotes.store.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID>{
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}