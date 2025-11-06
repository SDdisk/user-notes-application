package com.github.sddisk.usernotes.api.controller

import com.github.sddisk.usernotes.api.dto.AuthResponse
import com.github.sddisk.usernotes.api.dto.LoginRequestDto
import com.github.sddisk.usernotes.api.dto.RegisterRequestDto
import com.github.sddisk.usernotes.service.auth.AuthService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CookieValue
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
    fun register(@RequestBody request: RegisterRequestDto, servletResponse: HttpServletResponse): AuthResponse =
        authService.register(request, servletResponse)

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    fun login(@RequestBody request: LoginRequestDto, servletResponse: HttpServletResponse): AuthResponse =
        authService.login(request, servletResponse)

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    fun logout(servletResponse: HttpServletResponse) =
        authService.logout(servletResponse)

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    fun refreshToken(@CookieValue("refreshToken") refreshToken: String): AuthResponse =
        authService.refreshToken(refreshToken)
}