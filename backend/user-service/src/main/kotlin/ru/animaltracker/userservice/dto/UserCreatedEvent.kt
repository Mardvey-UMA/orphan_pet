package ru.animaltracker.userservice.dto

data class UserCreatedEvent(
    val email: String,
    val username: String
)
