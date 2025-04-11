package ru.animaltracker.userservice.dto

import java.time.LocalDate

data class AttributeHistoryDto(
    val value: String,
    val recordedAt: LocalDate,
    val changedBy: String
)
