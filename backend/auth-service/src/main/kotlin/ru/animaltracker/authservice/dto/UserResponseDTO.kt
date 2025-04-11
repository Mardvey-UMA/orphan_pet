package ru.animaltracker.authservice.dto
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import ru.animaltracker.authservice.entity.Role
import ru.animaltracker.authservice.enums.Provider
import ru.animaltracker.authservice.enums.UserRole
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserResponseDTO(
    var role: MutableSet<Role>, // Тут поменять
    var provider: Provider,
    var enabled: Boolean,
    var vkId: Long? = null,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime? = null,
    )
