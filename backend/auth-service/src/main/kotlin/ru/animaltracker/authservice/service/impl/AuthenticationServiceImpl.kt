package ru.animaltracker.authservice.service.impl

import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.animaltracker.authservice.entity.User
import ru.animaltracker.authservice.config.JwtConfig
import ru.animaltracker.authservice.dto.AuthRequestDTO
import ru.animaltracker.authservice.dto.AuthResponseDTO
import ru.animaltracker.authservice.enums.CookieName
import ru.animaltracker.authservice.exception.GlobalExceptionHandler
import ru.animaltracker.authservice.service.interfaces.AuthenticationService
import ru.animaltracker.authservice.service.JwtService
import ru.animaltracker.authservice.service.interfaces.TokenService
import ru.animaltracker.authservice.service.interfaces.UserService
import java.time.LocalDateTime

@Service
class AuthenticationServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService,
    private val jwtService: ru.animaltracker.authservice.service.JwtService,
    private val tokenService: TokenService,
    private val jwtConfig: ru.animaltracker.authservice.config.JwtConfig
) : AuthenticationService {

    override fun authenticate(request: AuthRequestDTO, response: HttpServletResponse): AuthResponseDTO {

        val identifier: String = request.indentifier
        val userEntity: User = if (identifier.contains("@")) {
            userService.findByEmail(identifier)
        } else {
            userService.findByUsername(identifier)
        } ?: throw UsernameNotFoundException("User with identifier '$identifier' not found")

        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                userEntity.username,
                request.password,
            )
        )
        val user = auth.principal as User

        val accessToken: String = jwtService.generateAccessToken(user)
        val refreshToken: String = jwtService.generateRefreshToken(user)

        response.addCookie(jwtService.createHttpOnlyCookie(CookieName.ACCESS_TOKEN.name, accessToken))
        response.addCookie(jwtService.createHttpOnlyCookie(CookieName.REFRESH_TOKEN.name, refreshToken))

        tokenService.saveRefreshToken(user, refreshToken)

        return AuthResponseDTO(
            accessToken = accessToken,
            issuedAt = LocalDateTime.now(),
            accessExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.expiration),
            refreshToken = refreshToken,
            refreshExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.refreshExpiration)
        )
    }
}
