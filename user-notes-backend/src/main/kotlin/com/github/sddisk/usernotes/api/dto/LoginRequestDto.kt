package com.github.sddisk.usernotes.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @field:NotBlank(message = "Email cannot be empty")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Password cannot be empty")
    val password: String
) {
    override fun toString(): String = "LoginRequestDto(email=$email, password=[HIDDEN])"
}