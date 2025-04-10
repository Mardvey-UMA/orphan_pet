package ru.doedating.authservice.dto

data class UserCreatedEvent(
    val username: String,
    val email: String,
)
