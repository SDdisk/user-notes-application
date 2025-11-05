package com.github.sddisk.usernotes.api.controller

import com.github.sddisk.usernotes.api.dto.AuthResponse
import com.github.sddisk.usernotes.api.dto.LoginRequestDto
import com.github.sddisk.usernotes.api.dto.RegisterRequestDto
import com.github.sddisk.usernotes.service.auth.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/*
    TODO -> exception handling
 */

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: RegisterRequestDto): AuthResponse =
        authService.register(request)

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    fun login(@RequestBody request: LoginRequestDto): AuthResponse =
        authService.login(request)

    // TODO -> logout

    // TODO -> refresh token
}