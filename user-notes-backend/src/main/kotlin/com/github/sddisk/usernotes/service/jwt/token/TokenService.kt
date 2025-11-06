package com.github.sddisk.usernotes.service.jwt.token

import com.github.sddisk.usernotes.service.jwt.JwtService
import io.jsonwebtoken.Claims
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val jwtService: JwtService,
) {

    fun createRefreshTokenCookie(refreshToken: String, servletResponse: HttpServletResponse) =
        Cookie("refreshToken", refreshToken).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = jwtService.getClaim(refreshToken, Claims::getExpiration)
                ?.time?.toInt() ?: 0
        }.also { cookie ->
            log.info("Cookie added")
            servletResponse.addCookie(cookie)
        }

    fun deleteRefreshTokenCookie(servletResponse: HttpServletResponse) =
        Cookie("refreshToken", "").apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 0
        }.also { cookie ->
            log.info("Cookie deleted")
            servletResponse.addCookie(cookie)
        }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}