package ru.doedating.userservice.dto

data class UserUpdateDto(
    val firstName: String?,
    val lastName: String?,
    val city: String?,
    val aboutMe: String?
)
