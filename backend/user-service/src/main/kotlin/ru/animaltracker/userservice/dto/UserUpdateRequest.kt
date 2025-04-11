package ru.animaltracker.userservice.dto

import jakarta.validation.constraints.Size

data class UserUpdateRequest(
    @field:Size(max = 255)
    val firstName: String?,

    @field:Size(max = 255)
    val lastName: String?,

    @field:Size(max = 255)
    val city: String?,

    @field:Size(max = 255)
    val aboutMe: String?
)

