package ru.animaltracker.userservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import java.math.BigDecimal
import java.time.LocalDate

data class StatusLogCreateRequest(
    @field:NotBlank
    val notes: String?,
    val logDate: LocalDate?,
    val massChange: BigDecimal?,
    val heightChange: BigDecimal?,
    val temperatureChange: BigDecimal?,
    val activityLevelChange: Int?,
    val appetiteLevelChange: Int?
)