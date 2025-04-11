package ru.animaltracker.userservice.dto

data class UserDto(
    val email: String?,
    val username: String?,
    val firstName: String?,
    val lastName: String?,
    val city: String?,
    val aboutMe: String?
)