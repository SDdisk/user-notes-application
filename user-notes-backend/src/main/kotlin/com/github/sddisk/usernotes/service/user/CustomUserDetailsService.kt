package com.github.sddisk.usernotes.service.user

import com.github.sddisk.usernotes.store.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

typealias SecurityUser = org.springframework.security.core.userdetails.User

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val foundUser = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User with email=$username not found in repository.")

        return SecurityUser.builder()
            .username(foundUser.email)
            .password(foundUser.password)
            .roles(foundUser.role.name)
            .build()
    }

}