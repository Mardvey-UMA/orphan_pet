package ru.animaltracker.userservice.dto

data class AnimalAnalyticsResponse(
    val attributeName: String,
    val changes: List<AttributeChange>,
    val stats: AttributeStats?
)