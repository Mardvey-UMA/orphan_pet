package ru.animaltracker.userservice.dto

data class ParameterStats(
    val minValue: String?,
    val maxValue: String?,
    val avgValue: Double?,
    val firstValue: String?,
    val lastValue: String?
)