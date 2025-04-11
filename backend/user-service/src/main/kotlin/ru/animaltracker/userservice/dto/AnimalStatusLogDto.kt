package ru.animaltracker.userservice.dto

import java.time.LocalDate

data class AnimalStatusLogDto(
    val logDate: LocalDate,
    val notes: String?,
    val animalId: Long
)
