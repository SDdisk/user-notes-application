package com.github.sddisk.usernotes.api.dto.user

import java.util.UUID

data class UserResponseDto(
    val id: UUID?,
    val email: String,
    val username: String,
)