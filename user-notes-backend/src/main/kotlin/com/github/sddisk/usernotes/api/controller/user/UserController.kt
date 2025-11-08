package com.github.sddisk.usernotes.api.controller.user

import com.github.sddisk.usernotes.api.dto.user.UserResponseDto
import com.github.sddisk.usernotes.service.user.UserService
import com.github.sddisk.usernotes.store.entity.user.User
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    fun currentUser(@AuthenticationPrincipal userDetails: UserDetails): UserResponseDto =
        userService.findByEmail(userDetails.username).toResponseDto()


    private fun User.toResponseDto() = UserResponseDto(
        id = id,
        email = email,
        username = username,
    )
}