package ru.animaltracker.authservice.service.interfaces

import jakarta.servlet.http.HttpServletResponse
import ru.animaltracker.authservice.dto.AuthResponseDTO
import ru.animaltracker.authservice.entity.Token
import ru.animaltracker.authservice.entity.User

interface TokenService {
    fun saveRefreshToken(user: User, refreshToken: String)
    fun revokeRefreshToken(token: String)
    fun findRefreshToken(token: String): Token?
    fun revokeAllRefreshTokens(user: User)
    fun refreshToken(refreshToken: String, response: HttpServletResponse): AuthResponseDTO
}
