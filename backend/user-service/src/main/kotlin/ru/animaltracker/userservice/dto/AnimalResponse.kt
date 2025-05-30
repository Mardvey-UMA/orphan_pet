package ru.animaltracker.userservice.dto

import java.math.BigDecimal
import java.time.LocalDate

data class AnimalResponse(
    var id: Long = 0,
    var name: String? = null,
    var description: String? = null,
    var birthDate: LocalDate? = null,
    var mass: BigDecimal? = null,
    var height: BigDecimal? = null,
    var temperature: BigDecimal? = null,
    var activityLevel: Int? = null,
    var appetiteLevel: Int? = null,
    var attributes: List<AttributeResponse> = emptyList(),
    var photos: List<String> = emptyList(),
    var documents: List<String> = emptyList(),
)