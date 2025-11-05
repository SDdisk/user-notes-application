package com.github.sddisk.usernotes.service.auth

import com.github.sddisk.usernotes.api.dto.AuthResponse
import com.github.sddisk.usernotes.api.dto.LoginRequestDto
import com.github.sddisk.usernotes.api.dto.RegisterRequestDto
import com.github.sddisk.usernotes.exception.BadCredentialsException
import com.github.sddisk.usernotes.exception.UserAlreadyExistsException
import com.github.sddisk.usernotes.exception.UserNotFoundException
import com.github.sddisk.usernotes.store.entity.User
import com.github.sddisk.usernotes.store.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    // register
    fun register(request: RegisterRequestDto): AuthResponse {
        val isExist = userRepository.existsByEmail(request.email)

        if (isExist) throw UserAlreadyExistsException(
            "User with email=${request.email} already exists in repository."
        )

        val user = request.toUser()
        val saved = userRepository.save(user)
        return AuthResponse(
            id = saved.id,
            email = saved.email,
            username = saved.username,
            role = saved.role
        )
    }

    // login
    // simulating
    fun login(request: LoginRequestDto): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw UserNotFoundException("User with email=${request.email} not found in repository.")

        if (passwordEncoder.matches(request.password, user.password).not())
            throw BadCredentialsException("Incorrect password to user with email=${user.email}")

        return AuthResponse(
            id = user.id,
            email = user.email,
            username = user.username,
            role = user.role
        )
    }

    private fun RegisterRequestDto.toUser() = User(
        email = email,
        username = username,
        password = passwordEncoder.encode(password)
    )
}