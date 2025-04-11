package ru.animaltracker.userservice.dto

import java.math.BigDecimal
import java.time.LocalDate

data class AnimalUpdateRequest(
    val name: String?,
    val description: String?,
    val birthDate: LocalDate?,
    val mass: BigDecimal?
)