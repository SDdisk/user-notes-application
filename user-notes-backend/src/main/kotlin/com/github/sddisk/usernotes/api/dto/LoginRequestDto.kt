package com.github.sddisk.usernotes.api.dto

data class LoginRequestDto(
    val email: String,
    val password: String
) {
    override fun toString(): String = "LoginRequestDto(email=$email, password=[HIDDEN])"
}