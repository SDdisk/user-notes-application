package com.github.sddisk.usernotesmailsender.dto

data class EmailDto(
    val to: String,
    val subject: String,
    val text: String,
)
