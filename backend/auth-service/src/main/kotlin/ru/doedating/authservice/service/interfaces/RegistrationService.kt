package ru.doedating.authservice.service.interfaces

import ru.doedating.authservice.dto.UserRequestDTO
import ru.doedating.authservice.dto.UserResponseDTO


interface RegistrationService {
    fun register(request: UserRequestDTO): UserResponseDTO
}
