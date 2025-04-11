package ru.doedating.userservice.dto

data class AnimalDto(
    val name: String?,
    val description: String?,
    val attributes: Map<String, String> = emptyMap()
)
