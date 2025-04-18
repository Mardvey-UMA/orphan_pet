package ru.animaltracker.authservice.service.impl

import org.springframework.stereotype.Service
import ru.animaltracker.authservice.dto.UserCreatedEvent
//import ru.animaltracker.authservice.kafka.KafkaProducer
import ru.animaltracker.authservice.service.interfaces.ActivationService
import ru.animaltracker.authservice.service.interfaces.RegistrationService
import ru.animaltracker.authservice.service.interfaces.UserService
import ru.animaltracker.authservice.dto.UserRequestDTO
import ru.animaltracker.authservice.dto.UserResponseDTO
import ru.animaltracker.authservice.service.MessageProducerService
import java.time.LocalDateTime

@Service
class RegistrationServiceImpl(
    private val userService: UserService,
    private val activationService: ActivationService,
    private val messageProducerService: MessageProducerService
) : RegistrationService {

    override fun register(request: UserRequestDTO): UserResponseDTO {

        val rawPassword = request.password ?: throw IllegalArgumentException("Password is required")

        val user = userService.registerUser(
            email = request.email,
            username = request.username,
            rawPassword = rawPassword
        )

        val activationToken = activationService.generateAndSaveActivationToken(user)

        activationService.sendActivationEmail(user, activationToken)

        // TODO( ДЛЯ ТЕСТОВ ИЗМЕНИТЬ ЛОГИКУ)...

        return UserResponseDTO(
            role = user.roles,
            provider = user.provider,
            enabled = user.enabled,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

}
