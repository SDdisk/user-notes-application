package com.github.sddisk.usernotes.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthResponse(
    @JsonProperty("token")
    val accessToken: String,
)
