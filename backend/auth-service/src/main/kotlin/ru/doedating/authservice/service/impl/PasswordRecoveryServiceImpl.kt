package ru.doedating.authservice.service.impl

import org.springframework.stereotype.Service
import ru.doedating.authservice.entity.User
import ru.doedating.authservice.exception.GlobalExceptionHandler
import ru.doedating.authservice.service.PasswordRecoveryHelper
import ru.doedating.authservice.service.interfaces.PasswordRecoveryService
import ru.doedating.authservice.service.interfaces.UserService

@Service
class PasswordRecoveryServiceImpl(
    private val userService: UserService,
    private val passwordRecoveryHelper: PasswordRecoveryHelper
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
