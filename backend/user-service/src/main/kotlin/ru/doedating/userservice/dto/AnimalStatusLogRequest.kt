package ru.doedating.userservice.dto

import java.time.LocalDate

data class AnimalStatusLogRequest(
    val logDate: LocalDate,
    val notes: String,
    val photoIds: List<Long> = emptyList(),
    val documentIds: List<Long> = emptyList()
)