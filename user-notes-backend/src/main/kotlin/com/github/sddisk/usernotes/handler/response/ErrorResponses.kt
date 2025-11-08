package com.github.sddisk.usernotes.handler.response

import java.time.LocalDateTime

data class ErrorResponse(
    val message: String,
    val timestamp: LocalDateTime,
    val details: List<String> = emptyList(),
)