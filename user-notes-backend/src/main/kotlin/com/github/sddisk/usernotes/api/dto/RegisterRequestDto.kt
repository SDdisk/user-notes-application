package com.github.sddisk.usernotes.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequestDto(
    @field:Size(min = 3, max = 64, message = "Username must be between 3 and 64 characters")
    val username: String,

    @field:NotBlank(message = "Email cannot be empty")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:Size(min = 10, message = "Password must be at least 10 characters long")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!*()_\\-\\[\\]{};':\"\\\\|,.<>/?]).*$",
        message = "Password must contain at least 1 digit, 1 lowercase letter, 1 uppercase letter, and 1 special character"
    )
    val password: String,
) {
    override fun toString(): String = "RegisterRequestDto(username=$username, email=$email, password=[HIDDEN])"
}
