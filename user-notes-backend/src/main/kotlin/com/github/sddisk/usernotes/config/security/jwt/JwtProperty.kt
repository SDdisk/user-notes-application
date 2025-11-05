package com.github.sddisk.usernotes.config.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
class JwtProperty(
    val secretKey: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long, // TODO -> refresh token
)