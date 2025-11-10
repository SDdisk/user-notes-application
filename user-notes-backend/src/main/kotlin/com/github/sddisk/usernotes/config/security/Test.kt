package com.github.sddisk.usernotes.config.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

fun main() {
    val password = "sddisk-is-admin"
    val encodedPassword = BCryptPasswordEncoder().encode(password)
    println(encodedPassword)
}