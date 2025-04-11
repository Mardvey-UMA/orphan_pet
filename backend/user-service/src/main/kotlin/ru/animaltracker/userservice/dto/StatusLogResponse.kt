package ru.animaltracker.userservice.dto

import java.time.LocalDate

data class StatusLogResponse(
    val id: Long,
    val logDate: LocalDate,
    val notes: String?,
    val photos: List<String>,
    val documents: List<String>
)