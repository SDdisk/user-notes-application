package com.github.sddisk.usernotes.service.auth

import com.github.sddisk.usernotes.api.dto.AuthResponse
import com.github.sddisk.usernotes.api.dto.LoginRequestDto
import com.github.sddisk.usernotes.api.dto.RegisterRequestDto
import com.github.sddisk.usernotes.exception.UserAlreadyExistsException
import com.github.sddisk.usernotes.service.jwt.JwtService
import com.github.sddisk.usernotes.service.user.UserService
import com.github.sddisk.usernotes.store.entity.User
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
) {
    // register
    fun register(request: RegisterRequestDto): AuthResponse {
        log.info("Register with request=$request") // TODO -> print only email in logs, cuz need to hide the password

        if (userService.existByEmail(request.email)) {
            log.error("User with request email=${request.email} already exists")
            throw UserAlreadyExistsException("User with email=${request.email} already exists in repository.")
        }

        log.info("Mapping request to user")
        val user = request.toUser()

        log.info("Saving user=$user")
        val saved = userService.save(user)
        log.info("User=$saved saved")

        log.info("Loading user details")
        val userDetails = userService.loadUserByUsername(saved.email)
        log.info("Loaded user details. Username=${userDetails.username}")

        log.info("Return authentication response with access token after successful registration :)")
        return AuthResponse(
            accessToken = jwtService.generateAccessToken(userDetails)
        )
    }

    // login
    fun login(request: LoginRequestDto): AuthResponse {
        log.info("Login with request=$request") // TODO -> print only email in logs, cuz need to hide the password

        log.info("Authentication...")
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        log.info("Loading user details")
        val userDetails = userService.loadUserByUsername(request.email)
        log.info("Loaded user details. Username=${userDetails.username}")

        log.info("Return authentication response with access token after successful login :P")
        return AuthResponse(
            accessToken = jwtService.generateAccessToken(userDetails)
        )
    }

    // TODO -> logout

    // TODO -> refresh token


    private fun RegisterRequestDto.toUser() = User(
        email = email,
        username = username,
        password = passwordEncoder.encode(password)
    )

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}