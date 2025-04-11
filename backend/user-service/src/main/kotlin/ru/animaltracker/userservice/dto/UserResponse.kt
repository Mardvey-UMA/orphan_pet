package ru.animaltracker.userservice.dto

data class UserResponse(
    val username: String,
    val firstName: String?,
    val lastName: String?,
    val city: String?,
    val aboutMe: String?,
    val photoUrl: String?
)