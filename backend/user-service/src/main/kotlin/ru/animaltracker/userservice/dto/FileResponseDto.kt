package ru.animaltracker.userservice.dto

import java.time.LocalDate

data class FileResponseDto(
    val id: Long,
    val url: String,
    val description: String?,
    val createdAt: LocalDate?,
    val contentType: String,
    val originalFilename: String
)