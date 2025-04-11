package ru.animaltracker.authservice.service.interfaces

import jakarta.servlet.http.HttpServletResponse
import ru.doedating.authservice.dto.AuthResponseDTO
import ru.doedating.authservice.entity.Token
import ru.doedating.authservice.entity.User

interface TokenService {
    fun saveRefreshToken(user: User, refreshToken: String)
    fun revokeRefreshToken(token: String)
    fun findRefreshToken(token: String): Token?
    fun revokeAllRefreshTokens(user: User)
    fun refreshToken(refreshToken: String, response: HttpServletResponse): AuthResponseDTO
}
