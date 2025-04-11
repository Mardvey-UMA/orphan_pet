package ru.animaltracker.userservice.dto

import java.time.LocalDate

data class StatusLogUpdateRequest(
    val notes: String?,
    val logDate: LocalDate?
)