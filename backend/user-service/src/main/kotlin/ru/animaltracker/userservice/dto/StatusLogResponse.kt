package ru.animaltracker.userservice.dto

import java.math.BigDecimal
import java.time.LocalDate

data class StatusLogResponse(
    var id: Long = 0,
    var logDate: LocalDate = LocalDate.now(),
    var notes: String? = null,
    var massChange: BigDecimal? = null,
    var heightChange: BigDecimal? = null,
    var temperatureChange: BigDecimal? = null,
    var activityLevelChange: Int? = null,
    var appetiteLevelChange: Int? = null,
    var photos: List<String> = emptyList(),
    var documents: List<String> = emptyList(),
    var parameterChanges: List<ParameterChangeResponse> = emptyList()
)
