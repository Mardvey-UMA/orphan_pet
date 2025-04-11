package ru.animaltracker.authservice.service.interfaces

import ru.animaltracker.authservice.dto.UserRequestDTO
import ru.animaltracker.authservice.dto.UserResponseDTO


interface RegistrationService {
    fun register(request: UserRequestDTO): UserResponseDTO
}
