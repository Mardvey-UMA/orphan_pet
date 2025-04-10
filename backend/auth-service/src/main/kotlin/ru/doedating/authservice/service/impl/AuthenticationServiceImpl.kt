package ru.doedating.authservice.service.impl

import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.doedating.authservice.entity.User
import ru.doedating.authservice.config.JwtConfig
import ru.doedating.authservice.dto.AuthRequestDTO
import ru.doedating.authservice.dto.AuthResponseDTO
import ru.doedating.authservice.enums.CookieName
import ru.doedating.authservice.exception.GlobalExceptionHandler
import ru.doedating.authservice.service.interfaces.AuthenticationService
import ru.doedating.authservice.service.JwtService
import ru.doedating.authservice.service.interfaces.TokenService
import ru.doedating.authservice.service.interfaces.UserService
import java.time.LocalDateTime

@Service
class AuthenticationServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService,
    private val jwtService: ru.doedating.authservice.service.JwtService,
    private val tokenService: TokenService,
    private val jwtConfig: JwtConfig
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
