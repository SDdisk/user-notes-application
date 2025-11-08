package com.github.sddisk.usernotes.service.user

import com.github.sddisk.usernotes.exception.UserNotFoundException
import com.github.sddisk.usernotes.store.entity.user.User
import com.github.sddisk.usernotes.store.repository.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

typealias SecurityUser = org.springframework.security.core.userdetails.User

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserService {

    override fun save(user: User): User =
        userRepository.save(user.withHashPassword())
            .also { user ->
                log.info("Saved user=$user")
            }

    override fun findByEmail(email: String): User =
        userRepository.findByEmail(email)
            ?: throw UserNotFoundException("User with email=$email not found")

    override fun existByEmail(email: String): Boolean =
        userRepository.existsByEmail(email)

    override fun findById(id: UUID): User =
        userRepository.findByIdOrNull(id)
            ?: throw UserNotFoundException("User with id=$id not found")

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User with email=$username not found")

        return SecurityUser.builder()
            .username(user.email)
            .password(user.password)
            .roles(user.role.name)
            .build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    private fun User.withHashPassword() = this.apply {
        password = passwordEncoder.encode(this.password)
    }
}