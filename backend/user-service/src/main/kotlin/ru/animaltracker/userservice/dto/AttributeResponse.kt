package ru.animaltracker.userservice.dto

data class AttributeResponse(
    var id: Short = 0,
    var name: String? = null,
    var value: String? = null
)