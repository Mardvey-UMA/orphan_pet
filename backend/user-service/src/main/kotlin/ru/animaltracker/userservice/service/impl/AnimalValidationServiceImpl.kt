package ru.animaltracker.userservice.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.animaltracker.userservice.entity.Animal
import ru.animaltracker.userservice.entity.AnimalStatusLog
import ru.animaltracker.userservice.entity.User
import ru.animaltracker.userservice.repository.AnimalRepository
import ru.animaltracker.userservice.repository.AnimalStatusLogRepository
import ru.animaltracker.userservice.repository.UserRepository
import ru.animaltracker.userservice.service.interfaces.AnimalValidationService
import java.nio.file.AccessDeniedException


@Service
class AnimalValidationServiceImpl (
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val statusLogRepository: AnimalStatusLogRepository
): AnimalValidationService {


    @Transactional(readOnly = true)
    override fun validateUserAndAnimal(username: String, animalId: Long): Pair<User, Animal> {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        val animal = animalRepository.findById(animalId)
            .orElseThrow { EntityNotFoundException("Animal not found") }

        if (animal.animalUsers.none { it.user?.id == user.id }) {
            throw AccessDeniedException("User doesn't have access to this animal")
        }

        return user to animal
    }

    @Transactional(readOnly = true)
    override fun validateUserAndStatusLog(
        username: String,
        animalId: Long,
        statusLogId: Long
    ): Triple<User, Animal, AnimalStatusLog> {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val statusLog = statusLogRepository.findById(statusLogId)
            .orElseThrow { EntityNotFoundException("Status log not found") }

        if (statusLog.animal.id != animalId) {
            throw AccessDeniedException("Status log doesn't belong to this animal")
        }

        return Triple(user, animal, statusLog)
    }
}