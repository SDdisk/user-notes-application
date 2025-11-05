package com.github.sddisk.usernotes.api.dto

import com.github.sddisk.usernotes.store.entity.Role
import java.util.UUID

data class AuthResponse(
    val id: UUID?,
    val username: String,
    val email: String,
    val role: Role,
)
