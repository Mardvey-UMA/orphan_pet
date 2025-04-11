package ru.animaltracker.authservice.service.impl

import org.springframework.stereotype.Service
//import ru.doedating.authservice.kafka.KafkaProducer
import ru.doedating.authservice.service.interfaces.ActivationService
import ru.doedating.authservice.service.interfaces.RegistrationService
import ru.doedating.authservice.service.interfaces.UserService
import ru.doedating.authservice.dto.UserRequestDTO
import ru.doedating.authservice.dto.UserResponseDTO
import java.time.LocalDateTime

@Service
class RegistrationServiceImpl(
    private val userService: UserService,
    private val activationService: ActivationService,
    //private val kafkaProducer: KafkaProducer
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

        return UserResponseDTO(
            role = user.roles,
            provider = user.provider,
            enabled = user.enabled,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

}
