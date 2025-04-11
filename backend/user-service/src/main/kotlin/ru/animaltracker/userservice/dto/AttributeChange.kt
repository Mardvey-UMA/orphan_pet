package ru.animaltracker.userservice.dto

import java.time.LocalDate

data class AttributeChange(
    val date: LocalDate,
    val value: String,
    val changedBy: String
)