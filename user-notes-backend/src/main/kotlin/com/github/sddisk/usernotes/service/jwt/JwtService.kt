package com.github.sddisk.usernotes.service.jwt

import com.github.sddisk.usernotes.config.security.jwt.JwtProperty
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService(
    private val jwtProperty: JwtProperty
) {
    private val secretKey = Keys.hmacShaKeyFor(
        jwtProperty.secretKey.toByteArray()
    )

    // access token
    fun generateAccessToken(userDetails: UserDetails) =
        buildToken(
            userDetails = userDetails,
            expirationDate = Date(System.currentTimeMillis() + jwtProperty.accessTokenExpiration)
        ).also {
            log.info("Created access token")
        }

    // refresh token
    fun generateRefreshToken(userDetails: UserDetails) =
        buildToken(
            userDetails = userDetails,
            expirationDate = Date(System.currentTimeMillis() + jwtProperty.refreshTokenExpiration)
        ).also {
            log.info("Created refresh token")
        }

    fun createRefreshTokenCookie(refreshToken: String, servletResponse: HttpServletResponse) =
        Cookie("refreshToken", refreshToken).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = getClaim(refreshToken, Claims::getExpiration)
                ?.time?.toInt() ?: 0
        }.also { cookie ->
            servletResponse.addCookie(cookie)
            log.info("Refresh token cookie created")
        }

    fun deleteRefreshTokenCookie(servletResponse: HttpServletResponse) =
        Cookie("refreshToken", "").apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 0
        }.also { cookie ->
            servletResponse.addCookie(cookie)
            log.info("Refresh token cookie deleted")
        }

    // is token valid
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean =
        userDetails.username == extractEmail(token) && isTokenExpired(token).not()

    // is token expired
    fun isTokenExpired(token: String): Boolean =
        getClaim(token, Claims::getExpiration)
            ?.before(Date()) ?: true

    // get email
    fun extractEmail(token: String): String? =
        getClaim(token, Claims::getSubject)

    // get claim
    fun <T> getClaim(token: String, claimResolver: (Claims) -> T): T? =
        claimResolver(getAllClaims(token))

    // get claims
    private fun getAllClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

    // build token
    private fun buildToken(userDetails: UserDetails, expirationDate: Date): String =
        Jwts.builder()
            .subject(userDetails.username)
            .issuedAt(Date())
            .expiration(expirationDate)
            .signWith(secretKey)
            .compact()

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}