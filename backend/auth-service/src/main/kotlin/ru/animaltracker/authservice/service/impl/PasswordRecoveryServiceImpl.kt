package ru.animaltracker.authservice.service.impl

import org.springframework.stereotype.Service
import ru.animaltracker.authservice.entity.User
import ru.animaltracker.authservice.exception.GlobalExceptionHandler
import ru.animaltracker.authservice.service.PasswordRecoveryHelper
import ru.animaltracker.authservice.service.interfaces.PasswordRecoveryService
import ru.animaltracker.authservice.service.interfaces.UserService

@Service
class PasswordRecoveryServiceImpl(
    private val userService: UserService,
    private val passwordRecoveryHelper: ru.animaltracker.authservice.service.PasswordRecoveryHelper
) : PasswordRecoveryService {

    override fun initiatePasswordRecovery(identifier: String) {
        val user: User = if (identifier.contains("@")) {
            userService.findByEmail(identifier)
                ?: throw GlobalExceptionHandler.UserNotFoundException("User with email $identifier not found")
        } else {
            userService.findByUsername(identifier)
                ?: throw GlobalExceptionHandler.UserNotFoundException("User with username $identifier not found")
        }
        val recoveryToken: String = passwordRecoveryHelper.generateAndSaveRecoveryToken(user)
        passwordRecoveryHelper.sendRecoveryEmail(user, recoveryToken)
    }

    override fun resetPassword(token: String, newPassword: String) {
        passwordRecoveryHelper.resetPassword(token, newPassword)
    }
}
