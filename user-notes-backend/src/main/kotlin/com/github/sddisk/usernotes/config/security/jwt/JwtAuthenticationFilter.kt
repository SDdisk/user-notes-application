package com.github.sddisk.usernotes.config.security.jwt

import com.github.sddisk.usernotes.service.jwt.JwtService
import com.github.sddisk.usernotes.service.user.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userService: UserService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.extractToken()
            ?: run {
                filterChain.doFilter(request, response)
                return
            }

        val email = jwtService.extractEmail(token)

        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userService.loadUserByUsername(email)
            if (jwtService.isTokenValid(token, userDetails)) {
                updateContext(userDetails, request)
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun HttpServletRequest.extractToken(): String? {
        val authenticationHeader = this.getHeader("Authorization")
        if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")){
            return authenticationHeader.substringAfter("Bearer ")
        }
        return null
    }

    private fun updateContext(userDetails: UserDetails, request: HttpServletRequest) {
        val authenticationToken = UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.authorities
        )

        authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }
}