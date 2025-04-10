package ru.doedating.authservice.service

import org.springframework.stereotype.Service
import ru.doedating.authservice.entity.MailToken
import ru.doedating.authservice.entity.User
import ru.doedating.authservice.enums.MailTokenType
import ru.doedating.authservice.repository.MailTokenRepository
import ru.doedating.authservice.config.EmailConfig
import ru.doedating.authservice.enums.EmailTemplateName
import ru.doedating.authservice.exception.GlobalExceptionHandler
import ru.doedating.authservice.service.interfaces.UserService
import java.security.SecureRandom
import java.time.LocalDateTime

@Service
class PasswordRecoveryHelper(
    private val mailTokenRepository: MailTokenRepository,
    private val userService: UserService,
    private val emailService: EmailService,
    private val emailConfig: EmailConfig
) {

    fun generateAndSaveRecoveryToken(user: User): String {
        deactivateExistingTokens(user, MailTokenType.RECOVERY)

        val generatedToken = generateRecoveryCode()
        val mailToken = MailToken(
            token = generatedToken,
            createdAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusSeconds(emailConfig.activationTokenExpiration),
            user = user,
            tokenType = MailTokenType.RECOVERY
        )
        mailTokenRepository.save(mailToken)
        return generatedToken
    }

    fun resetPassword(token: String, newPassword: String) {
        val mailToken = mailTokenRepository.findByToken(token)
            ?: throw GlobalExceptionHandler.InvalidTokenException("Invalid recovery token")

        if (LocalDateTime.now().isAfter(mailToken.expiresAt) || !mailToken.enabled) {
            throw GlobalExceptionHandler.InvalidTokenException("Recovery token expired or already used")
        }

        val user = mailToken.user
        userService.updatePassword(user, newPassword)
        mailToken.validatedAt = LocalDateTime.now()
        mailToken.enabled = false
        mailTokenRepository.save(mailToken)
    }

    fun sendRecoveryEmail(user: User, token: String) {
        emailService.sendEmail(
            to = user.email,
            username = user.username,
            emailTemplate = EmailTemplateName.RECOVERY_PASSWORD,
            confirmationUrl = emailConfig.activationUrl,
            activationCode = token,
            subject = "Password Recovery"
        )
    }

    private fun deactivateExistingTokens(user: User, tokenType: MailTokenType) {
        val activeTokens = mailTokenRepository.findByUserIdAndEnabledAndTokenType(
            userId = user.id!!,
            enabled = true,
            tokenType = tokenType
        )
        activeTokens.forEach { it.enabled = false }
        mailTokenRepository.saveAll(activeTokens)
    }

    private fun generateRecoveryCode(length: Int = 6): String {
        val secureRandom = SecureRandom()
        return (1..length)
            .map { secureRandom.nextInt(10) }
            .joinToString("")
    }
}
