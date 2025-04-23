package ru.animaltracker.userservice.dto

import java.math.BigDecimal

data class AnimalParametersUpdateRequest(
    val mass: BigDecimal?,
    val height: BigDecimal?,
    val temperature: BigDecimal?,
    val activityLevel: Int?,
    val appetiteLevel: Int?
)