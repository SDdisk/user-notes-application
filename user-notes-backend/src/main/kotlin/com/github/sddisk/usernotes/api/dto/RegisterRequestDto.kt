package com.github.sddisk.usernotes.api.dto

data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String,
)
