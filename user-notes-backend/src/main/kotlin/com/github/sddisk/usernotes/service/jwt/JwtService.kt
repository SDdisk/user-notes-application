package com.github.sddisk.usernotes.service.jwt

import com.github.sddisk.usernotes.config.security.jwt.JwtProperty
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
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
        )

    // refresh token
    fun generateRefreshToken(userDetails: UserDetails) =
        buildToken(
            userDetails = userDetails,
            expirationDate = Date(System.currentTimeMillis() + jwtProperty.refreshTokenExpiration)
        )

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
    fun getAllClaims(token: String): Claims =
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
}