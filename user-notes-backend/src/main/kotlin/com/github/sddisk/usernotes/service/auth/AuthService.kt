package com.github.sddisk.usernotes.service.auth

import com.github.sddisk.usernotes.api.dto.auth.AuthResponse
import com.github.sddisk.usernotes.api.dto.auth.LoginRequestDto
import com.github.sddisk.usernotes.api.dto.auth.RegisterRequestDto
import com.github.sddisk.usernotes.exception.UserAlreadyExistsException
import com.github.sddisk.usernotes.service.jwt.JwtService
import com.github.sddisk.usernotes.service.kafka.KafkaEmailService
import com.github.sddisk.usernotes.service.user.UserService
import com.github.sddisk.usernotes.store.entity.user.User
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val kafkaEmailService: KafkaEmailService,
) {

    fun register(request: RegisterRequestDto, servletResponse: HttpServletResponse): AuthResponse {
        log.info("Register with request=$request")

        if (userService.existByEmail(request.email)) {
            log.error("User with request email=${request.email} already exists")
            throw UserAlreadyExistsException("User with email=${request.email} already exists")
        }

        val user = request.toUser()
        val saved = userService.save(user)
        val userDetails = userService.loadUserByUsername(saved.email)

        jwtService.createRefreshTokenCookie(
            refreshToken = jwtService.generateRefreshToken(userDetails),
            servletResponse = servletResponse
        )

        val accessToken = jwtService.generateAccessToken(userDetails)

        kafkaEmailService.sendWelcomeEmail(saved.email, saved.username)

        log.info("User successfully registered")
        return AuthResponse(
            accessToken = accessToken
        )
    }

    fun login(request: LoginRequestDto, servletResponse: HttpServletResponse): AuthResponse {
        log.info("Login with request=$request")

        log.info("Authentication")
        val userDetails = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        ).principal as UserDetails

        jwtService.createRefreshTokenCookie(
            refreshToken = jwtService.generateRefreshToken(userDetails),
            servletResponse = servletResponse
        )

        val accessToken = jwtService.generateAccessToken(userDetails)

        log.info("User successfully login")
        return AuthResponse(
            accessToken = accessToken
        )
    }

    fun logout(servletResponse: HttpServletResponse) {
        log.info("Logout request")
        jwtService.deleteRefreshTokenCookie(servletResponse)
        log.info("Successful logout")
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        log.info("Request to refresh token")

        log.info("Extract email from refresh token")
        val email = jwtService.extractEmail(refreshToken)
            ?: run {
                log.error("Email not extracted from refresh token")
                throw MalformedJwtException("Invalid refresh token")
            }

        val userDetails = userService.loadUserByUsername(email)

        log.info("Validate refresh token with user details=$userDetails")
        if (jwtService.isTokenValid(refreshToken, userDetails).not()){
            log.error("Refresh token is not valid")
            throw SignatureException("Invalid or expired refresh token")
        }

        val accessToken = jwtService.generateAccessToken(userDetails)

        log.info("Token successfully refreshed")
        return AuthResponse(
            accessToken = accessToken
        )
    }

    private fun RegisterRequestDto.toUser() = User(
        email = email,
        username = username,
        password = password
    )

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}