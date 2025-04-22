package ru.animaltracker.userservice.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.animaltracker.userservice.dto.*
import ru.animaltracker.userservice.service.interfaces.StatusLogService


@RestController
@RequestMapping("/api/animals/{animalId}/status-logs")
class StatusLogController(
    private val statusLogService: StatusLogService
) {

    @PostMapping
    fun addStatusLog(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @Valid @RequestBody request: StatusLogCreateRequest
    ): ResponseEntity<StatusLogResponse> {
        return ResponseEntity.ok(statusLogService.addStatusLog(username, animalId, request))
    }

    @GetMapping
    fun getStatusLogs(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<List<StatusLogResponse>> {
        return ResponseEntity.ok(statusLogService.getStatusLogs(username, animalId))
    }

    @GetMapping("/{statusLogId}")
    fun getStatusLog(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long
    ): ResponseEntity<StatusLogResponse> {
        return ResponseEntity.ok(statusLogService.getStatusLog(statusLogId))
    }

    @PutMapping("/{statusLogId}")
    fun updateStatusLog(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long,
        @Valid @RequestBody request: StatusLogUpdateRequest
    ): ResponseEntity<StatusLogResponse> {
        return ResponseEntity.ok(
            statusLogService.updateStatusLog(username, animalId, statusLogId, request)
        )
    }

    @DeleteMapping("/{statusLogId}")
    fun deleteStatusLog(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long
    ): ResponseEntity<Void> {
        statusLogService.deleteStatusLog(username, animalId, statusLogId)
        return ResponseEntity.noContent().build()
    }
}