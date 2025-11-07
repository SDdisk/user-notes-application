package com.github.sddisk.usernotes.config.security

import com.github.sddisk.usernotes.config.security.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager =
        authConfig.authenticationManager

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthFilter: JwtAuthenticationFilter): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .cors { it.disable() }
            .headers { it.disable() } // fix h2 page error: ERR_BLOCKED_BY_RESPONSE
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(STATELESS) }
            .exceptionHandling { handle ->
                handle
                    .authenticationEntryPoint { _, _, _ ->
                        HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                    }
                    .accessDeniedHandler { request, response, accessDeniedException ->
                        response.status = HttpStatus.FORBIDDEN.value()
                        response.writer.write("Access denied")
                    }
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/auth/**", "/h2/**").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}