package ru.animaltracker.authservice.service.impl

import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import ru.animaltracker.authservice.config.JwtConfig
import ru.animaltracker.authservice.dto.AuthResponseDTO
import ru.animaltracker.authservice.entity.Token
import ru.animaltracker.authservice.entity.User
import ru.animaltracker.authservice.enums.CookieName
import ru.animaltracker.authservice.enums.TokenType
import ru.animaltracker.authservice.exception.GlobalExceptionHandler
import ru.animaltracker.authservice.repository.TokenRepository
import ru.animaltracker.authservice.service.JwtService
import ru.animaltracker.authservice.service.interfaces.TokenService
import ru.animaltracker.authservice.service.interfaces.UserService
import java.time.LocalDateTime

@Service
class TokenServiceImpl(
    private val tokenRepository: TokenRepository,
    private val userService: UserService,
    private val jwtService: ru.animaltracker.authservice.service.JwtService,
    private val jwtConfig: ru.animaltracker.authservice.config.JwtConfig,
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
