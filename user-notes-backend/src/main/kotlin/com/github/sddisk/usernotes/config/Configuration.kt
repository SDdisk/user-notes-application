package com.github.sddisk.usernotes.config

import com.github.sddisk.usernotes.config.security.jwt.JwtProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    value = [JwtProperty::class]
)
class Configuration