package com.github.sddisk.usernotes.service.user

import com.github.sddisk.usernotes.store.entity.User
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.UUID

interface UserService : UserDetailsService {
    fun save(user: User): User
    fun findByEmail(email: String): User
    fun existByEmail(email: String): Boolean
    fun findById(id: UUID): User
}