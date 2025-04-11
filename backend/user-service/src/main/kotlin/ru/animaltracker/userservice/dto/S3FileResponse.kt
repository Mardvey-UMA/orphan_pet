package ru.animaltracker.userservice.dto

data class S3FileResponse(
    val objectKey: String,
    val presignedUrl: String
)