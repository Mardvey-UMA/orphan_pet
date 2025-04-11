package ru.animaltracker.authservice.service.interfaces

import jakarta.servlet.http.HttpServletResponse
import ru.doedating.authservice.dto.AuthRequestDTO
import ru.doedating.authservice.dto.AuthResponseDTO

interface AuthenticationService {
    fun authenticate(request: AuthRequestDTO, response: HttpServletResponse): AuthResponseDTO
}
