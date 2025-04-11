package ru.animaltracker.userservice.dto

data class AnimalDto(
    val name: String?,
    val description: String?,
    // TODO доп поля
    val attributes: Map<String, String> = emptyMap()
)
