package com.github.sddisk.usernotes.handler.response

import java.time.LocalDateTime

data class ErrorResponse(
    val message: String,
    val timestamp: LocalDateTime
)

data class ErrorValidationResponse(
    val message: String,
    val details: List<String>,
    val timestamp: LocalDateTime
)