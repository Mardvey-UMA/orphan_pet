package ru.animaltracker.userservice.dto

data class AnimalShortResponse(
    val id: Long,
    val name: String?,
    val photoUrl: String?
)