package ru.animaltracker.userservice.service.interfaces

import ru.animaltracker.userservice.entity.*

interface AnimalValidationService {
    fun validateUserAndAnimal(username: String, animalId: Long): Pair<User, Animal>
    fun validateUserAndStatusLog(
        username: String,
        animalId: Long,
        statusLogId: Long
    ): Triple<User, Animal, AnimalStatusLog>
}