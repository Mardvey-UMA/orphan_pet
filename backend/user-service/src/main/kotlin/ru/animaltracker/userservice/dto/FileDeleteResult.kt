package ru.animaltracker.userservice.dto

data class FileDeleteResult(
    val success: Boolean,
    val message: String? = null
)
