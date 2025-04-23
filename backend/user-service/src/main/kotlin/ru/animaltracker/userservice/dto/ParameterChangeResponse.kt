package ru.animaltracker.userservice.dto

import java.time.LocalDate

data class ParameterChangeResponse(
    val parameterName: String,
    val oldValue: String,
    val newValue: String,
    val changedAt: LocalDate,
    val changedBy: String
)