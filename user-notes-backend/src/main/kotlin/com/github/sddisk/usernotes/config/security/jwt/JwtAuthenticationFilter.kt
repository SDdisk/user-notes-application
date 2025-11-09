package com.github.sddisk.usernotes.config.security.jwt

import com.github.sddisk.usernotes.service.jwt.JwtService
import com.github.sddisk.usernotes.service.user.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime

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
        try {
            val token = request.extractToken()
                ?: run {
                    filterChain.doFilter(request, response)
                    return
                }

            val email = jwtService.extractEmail(token)
                ?: throw BadCredentialsException("Invalid JWT token")

            SecurityContextHolder.getContext().authentication ?: run {
                val userDetails = userService.loadUserByUsername(email)
                if (jwtService.isTokenValid(token, userDetails)) {
                    updateContext(userDetails, request)
                }
            }

            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            handleException(response, ex)
        }
    }

    private fun HttpServletRequest.extractToken(): String? =
        this.getHeader("Authorization")?.let { authHeader ->
            val prefix = "Bearer "
            when {
                authHeader.startsWith(prefix) -> authHeader.substringAfter(prefix)
                else -> null
            }
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

    private fun handleException(response: HttpServletResponse, ex: Exception) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json"

        val jsonResponse = """
            {
                "message": "${ex.message}",
                "timestamp": "${LocalDateTime.now()}"
            }
        """.trimIndent()

        response.writer.apply {
            write(jsonResponse)
            flush()
        }
    }
}