package com.github.sddisk.usernotes.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .cors { it.disable() }
            .headers { it.disable() } // fix h2 page error: ERR_BLOCKED_BY_RESPONSE
            .sessionManagement { it.sessionCreationPolicy(STATELESS) }
            .authorizeHttpRequests { it.anyRequest().permitAll() } // temporary free access to all endpoints
            .formLogin { it.disable() }
            // jwt soon
            //.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}