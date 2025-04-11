package ru.doedating.userservice.dto

data class UserPhotoResponse(
    val photoId: Long,
    val presignedUrl: String
)
