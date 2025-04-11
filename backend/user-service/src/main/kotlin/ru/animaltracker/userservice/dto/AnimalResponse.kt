package ru.animaltracker.userservice.dto

import java.math.BigDecimal
import java.time.LocalDate

data class AnimalResponse(
    val id: Long,
    val name: String?,
    val description: String?,
    val birthDate: LocalDate?,
    val mass: BigDecimal?,
    val attributes: List<AttributeResponse>,
    val photos: List<String>
)