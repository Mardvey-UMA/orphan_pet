package ru.animaltracker.userservice.dto

data class AnimalAnalyticsResponse(
    val parameterName: String,
    val changes: List<ParameterChangeResponse>,
    val stats: ParameterStats?
)