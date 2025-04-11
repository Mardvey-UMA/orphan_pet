package ru.animaltracker.authservice.service.interfaces

import ru.animaltracker.authservice.entity.User

interface ActivationService {
    fun generateAndSaveActivationToken(user: User): String
    fun activateAccount(token: String)
    fun sendActivationEmail(user: User, token: String)
}
