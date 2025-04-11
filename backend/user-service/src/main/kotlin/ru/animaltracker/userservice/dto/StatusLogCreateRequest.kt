package ru.animaltracker.userservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate

data class StatusLogCreateRequest(
    @field:NotBlank
    val notes: String?,

    @field:PastOrPresent
    val logDate: LocalDate?
)