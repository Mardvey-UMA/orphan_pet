package ru.animaltracker.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.doedating.authservice.entity.MailToken
import ru.doedating.authservice.enums.MailTokenType

interface MailTokenRepository: JpaRepository<MailToken, Long> {
    fun findByToken(token: String): MailToken?
    fun findByUserId(userId: Long): List<MailToken>
    fun findByUserIdAndEnabledAndTokenType(
        userId: Long,
        enabled: Boolean,
        tokenType: MailTokenType
    ): List<MailToken>
}