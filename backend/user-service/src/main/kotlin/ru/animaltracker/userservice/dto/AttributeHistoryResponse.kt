package ru.animaltracker.userservice.dto

import java.time.LocalDate

data class AttributeHistoryResponse(
    val attributeName: String,
    val oldValue: String?,
    val changedAt: LocalDate,
    val changedBy: String
)