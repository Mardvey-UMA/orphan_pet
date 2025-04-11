package ru.animaltracker.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.authservice.entity.MailToken
import ru.animaltracker.authservice.enums.MailTokenType

interface MailTokenRepository: JpaRepository<MailToken, Long> {
    fun findByToken(token: String): MailToken?
    fun findByUserId(userId: Long): List<MailToken>
    fun findByUserIdAndEnabledAndTokenType(
        userId: Long,
        enabled: Boolean,
        tokenType: MailTokenType
    ): List<MailToken>
}