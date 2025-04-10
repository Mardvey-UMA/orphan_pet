package ru.doedating.authservice.service.impl

import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import ru.doedating.authservice.config.JwtConfig
import ru.doedating.authservice.dto.AuthResponseDTO
import ru.doedating.authservice.entity.Token
import ru.doedating.authservice.entity.User
import ru.doedating.authservice.enums.CookieName
import ru.doedating.authservice.enums.TokenType
import ru.doedating.authservice.exception.GlobalExceptionHandler
import ru.doedating.authservice.repository.TokenRepository
import ru.doedating.authservice.service.JwtService
import ru.doedating.authservice.service.interfaces.TokenService
import ru.doedating.authservice.service.interfaces.UserService
import java.time.LocalDateTime

@Service
class TokenServiceImpl(
    private val tokenRepository: TokenRepository,
    private val userService: UserService,
    private val jwtService: ru.doedating.authservice.service.JwtService,
    private val jwtConfig: JwtConfig,
) : TokenService {

    override fun saveRefreshToken(user: User, refreshToken: String) {
        val token = Token(
            user = user,
            token = refreshToken,
            tokenType = TokenType.REFRESH,
            expired = false,
            revoked = false,
            created = LocalDateTime.now()
        )
        tokenRepository.save(token)
    }

    override fun revokeRefreshToken(token: String) {
        val storedToken = tokenRepository.findByToken(token)
        storedToken?.let {
            it.revoked = true
            it.expired = true
            tokenRepository.save(it)
        }
    }

    override fun findRefreshToken(token: String): Token? = tokenRepository.findByToken(token)

    override fun revokeAllRefreshTokens(user: User) {
        val tokens = tokenRepository.findValidRefreshTokensByUser(user.id!!)
        tokens.forEach {
            it.revoked = true
            it.expired = true
        }
        tokenRepository.saveAll(tokens)
    }

    override fun refreshToken(refreshToken: String, response: HttpServletResponse): AuthResponseDTO {
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw GlobalExceptionHandler.InvalidTokenException("Invalid or expired refresh token")
        }

        val userEmail: String = jwtService.extractUsername(refreshToken)
            ?: throw GlobalExceptionHandler.InvalidTokenException("Invalid refresh token")

        val user: User = userService.findByEmail(userEmail)
            ?: throw GlobalExceptionHandler.InvalidTokenException("User not found")

        revokeRefreshToken(refreshToken)
        val newRefreshToken: String = jwtService.generateRefreshToken(user)
        saveRefreshToken(user, newRefreshToken)

        val newAccessToken: String = jwtService.generateAccessToken(user)

        response.addCookie(jwtService.createHttpOnlyCookie(CookieName.ACCESS_TOKEN.name, newAccessToken))
        response.addCookie(jwtService.createHttpOnlyCookie(CookieName.REFRESH_TOKEN.name, newRefreshToken))

        return AuthResponseDTO(
            accessToken = newAccessToken,
            issuedAt = LocalDateTime.now(),
            accessExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.expiration),
            refreshToken = newRefreshToken,
            refreshExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.refreshExpiration)
        )
    }
}
