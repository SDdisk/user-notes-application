package com.github.sddisk.usernotes.api.dto

data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String,
) {
    override fun toString(): String = "RegisterRequestDto(username=$username, email=$email, password=[HIDDEN])"
}
