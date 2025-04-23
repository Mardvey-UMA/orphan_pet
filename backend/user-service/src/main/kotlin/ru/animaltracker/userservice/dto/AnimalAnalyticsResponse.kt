package ru.animaltracker.userservice.dto

data class AnimalAnalyticsResponse(
    val attributeName: String,
    val changes: List<ParameterChangeResponse>,
    val stats: AttributeStats?
)