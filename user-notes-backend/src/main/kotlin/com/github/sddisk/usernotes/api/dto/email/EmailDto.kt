package com.github.sddisk.usernotes.api.dto.email

data class EmailDto(
    val to: String,
    val subject: String,
    val text: String,
)