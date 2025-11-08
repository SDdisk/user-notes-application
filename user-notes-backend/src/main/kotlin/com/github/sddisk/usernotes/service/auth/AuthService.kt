package com.github.sddisk.usernotes.service.auth

import com.github.sddisk.usernotes.api.dto.auth.AuthResponse
import com.github.sddisk.usernotes.api.dto.auth.LoginRequestDto
import com.github.sddisk.usernotes.api.dto.auth.RegisterRequestDto
import com.github.sddisk.usernotes.exception.UserAlreadyExistsException
import com.github.sddisk.usernotes.service.jwt.JwtService
import com.github.sddisk.usernotes.service.user.UserService
import com.github.sddisk.usernotes.store.entity.user.User
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
) {

    fun register(request: RegisterRequestDto, servletResponse: HttpServletResponse): AuthResponse {
        log.info("Register with request=$request")

        if (userService.existByEmail(request.email)) {
            log.error("User with request email=${request.email} already exists")
            throw UserAlreadyExistsException("User with email=${request.email} already exists")
        }

        log.info("Mapping request to user")
        val user = request.toUser()

        log.info("Saving user=$user")
        val saved = userService.save(user)

        log.info("Loading user details")
        val userDetails = userService.loadUserByUsername(saved.email)
        log.info("Loaded user details. Username=${userDetails.username}")

        log.info("Creating refresh token cookie")
        jwtService.createRefreshTokenCookie(
            refreshToken = jwtService.generateRefreshToken(userDetails),
            servletResponse = servletResponse
        )

        log.info("Return authentication response with access token after successful registration :)")
        return AuthResponse(
            accessToken = jwtService.generateAccessToken(userDetails)
        )
    }

    fun login(request: LoginRequestDto, servletResponse: HttpServletResponse): AuthResponse {
        log.info("Login with request=$request")

        log.info("Authentication")
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        log.info("Loading user details")
        val userDetails = userService.loadUserByUsername(request.email)
        log.info("Loaded user details. Username=${userDetails.username}")

        log.info("Creating refresh token cookie")
        jwtService.createRefreshTokenCookie(
            refreshToken = jwtService.generateRefreshToken(userDetails),
            servletResponse = servletResponse
        )

        log.info("Return authentication response with access token after successful login :P")
        return AuthResponse(
            accessToken = jwtService.generateAccessToken(userDetails)
        )
    }

    fun logout(servletResponse: HttpServletResponse) {
        log.info("Logout request -> deleting refresh token cookie")
        jwtService.deleteRefreshTokenCookie(servletResponse)
        log.info("Successful logout")
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        log.info("Request to refresh token=$refreshToken")

        log.info("Extract email from refresh token")
        val email = jwtService.extractEmail(refreshToken)
            ?: run {
                log.error("Email not extracted from refresh token")
                throw MalformedJwtException("Invalid refresh token")
            }

        log.info("Loading user details with email=$email")
        val userDetails = userService.loadUserByUsername(email)

        log.info("Validate refresh token with user details=$userDetails")
        if (jwtService.isTokenValid(refreshToken, userDetails).not()){
            log.error("Refresh token is not valid")
            throw SignatureException("Invalid or expired refresh token")
        }

        log.info("Return authentication response with access token after successful refresh token :3")
        return AuthResponse(
            accessToken = jwtService.generateAccessToken(userDetails)
        )
    }

    private fun RegisterRequestDto.toUser() = User(
        email = email,
        username = username,
        password = password // passEncoder -> userService
    )

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}