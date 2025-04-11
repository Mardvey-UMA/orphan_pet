package ru.animaltracker.userservice.dto

data class AnimalCreateRequest(
    val name: String,
    val description: String?,
    val attributes: Map<String, String>
)