package com.github.sddisk.usernotes.api.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthResponse(
    @field:JsonProperty("token")
    val accessToken: String,
)