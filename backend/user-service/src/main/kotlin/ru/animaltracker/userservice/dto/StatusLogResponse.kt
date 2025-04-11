package ru.animaltracker.userservice.dto

import java.time.LocalDate

data class StatusLogResponse(
    var id: Long = 0,
    var logDate: LocalDate = LocalDate.now(),
    var notes: String? = null,
    var photos: List<String> = emptyList(),
    var documents: List<String> = emptyList()
)
