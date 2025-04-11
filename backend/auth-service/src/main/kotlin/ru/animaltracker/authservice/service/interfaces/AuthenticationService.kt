package ru.animaltracker.authservice.service.interfaces

import jakarta.servlet.http.HttpServletResponse
import ru.animaltracker.authservice.dto.AuthRequestDTO
import ru.animaltracker.authservice.dto.AuthResponseDTO

interface AuthenticationService {
    fun authenticate(request: AuthRequestDTO, response: HttpServletResponse): AuthResponseDTO
}
