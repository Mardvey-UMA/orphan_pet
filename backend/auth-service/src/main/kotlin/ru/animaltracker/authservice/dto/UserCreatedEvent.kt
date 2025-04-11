package ru.animaltracker.authservice.dto

data class UserCreatedEvent(
    val username: String,
    val email: String,
)
