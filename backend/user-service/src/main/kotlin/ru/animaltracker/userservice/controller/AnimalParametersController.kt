package ru.animaltracker.userservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.animaltracker.userservice.dto.ParameterChangeResponse
import ru.animaltracker.userservice.dto.ParameterStats
import ru.animaltracker.userservice.service.interfaces.StatusLogService

@RestController
@RequestMapping("/api/animals/{animalId}/parameters")
class AnimalParametersController(
    private val statusLogService: StatusLogService
) {

    @GetMapping("/history/{parameterName}")
    fun getParameterHistory(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable parameterName: String
    ): ResponseEntity<List<ParameterChangeResponse>> {
        return ResponseEntity.ok(
            statusLogService.getParameterHistory(username, animalId, parameterName)
        )
    }

    @GetMapping("/stats/{parameterName}")
    fun getParameterStats(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable parameterName: String
    ): ResponseEntity<ParameterStats> {
        val history = statusLogService.getParameterHistory(username, animalId, parameterName)

        val numericValues = history
            .mapNotNull { it.newValue.toDoubleOrNull() }
            .takeIf { it.isNotEmpty() }

        val stats = numericValues?.let {
            ParameterStats(
                minValue = it.min().toString(),
                maxValue = it.max().toString(),
                avgValue = it.average(),
                firstValue = history.first().newValue,
                lastValue = history.last().newValue
            )
        } ?: ParameterStats(
            minValue = null,
            maxValue = null,
            avgValue = null,
            firstValue = history.firstOrNull()?.newValue,
            lastValue = history.lastOrNull()?.newValue
        )

        return ResponseEntity.ok(stats)
    }
}