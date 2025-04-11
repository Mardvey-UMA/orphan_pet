package ru.animaltracker.userservice.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.*
import ru.animaltracker.userservice.service.interfaces.AnimalService

@RestController
@RequestMapping("/api/v1/animals")
class AnimalController<StatusLogResponse : Any?>(
    private val animalService: AnimalService
) {

    @PostMapping
    suspend fun createAnimal(
        @RequestHeader("X-User-Name") username: String,
        @Valid @RequestBody request: AnimalCreateRequest
    ): ResponseEntity<AnimalResponse> {
        return ResponseEntity.ok(animalService.createAnimal(username, request))
    }

    @GetMapping("/{animalId}")
    suspend fun getAnimal(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<AnimalResponse> {
        return ResponseEntity.ok(animalService.getAnimal(username, animalId))
    }

    @GetMapping("/{animalId}/status-logs")
    suspend fun getAnimalStatusLogs(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<List<ru.animaltracker.userservice.dto.StatusLogResponse>>{
        return ResponseEntity.ok(animalService.getStatusLogs(username, animalId))
    }

    @DeleteMapping("/{animalId}")
    suspend fun deleteAnimal(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<Void> {
        animalService.deleteAnimal(username, animalId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{animalId}/photos")
    suspend fun uploadAnimalPhoto(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @RequestParam file: MultipartFile
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(animalService.addAnimalPhoto(username, animalId, file))
    }

    @PostMapping("/{animalId}/documents")
    suspend fun uploadAnimalDocument(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @RequestParam file: MultipartFile,
        @RequestParam type: String
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(animalService.addAnimalDocument(username, animalId, file, type))
    }

    @PostMapping("/{animalId}/status-logs")
    suspend fun addStatusLog(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @Valid @RequestBody request: StatusLogCreateRequest
    ): ResponseEntity<ru.animaltracker.userservice.dto.StatusLogResponse> {
        return ResponseEntity.ok(animalService.addStatusLog(username, animalId, request))
    }

    @PostMapping("/{animalId}/status-logs/{statusLogId}/photos")
    suspend fun uploadStatusLogPhoto(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long,
        @RequestParam file: MultipartFile
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(animalService.addStatusLogPhoto(username, animalId, statusLogId, file))
    }

    @PostMapping("/{animalId}/status-logs/{statusLogId}/documents")
    suspend fun uploadStatusLogDocument(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long,
        @RequestParam file: MultipartFile,
        @RequestParam type: String
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(animalService.addStatusLogDocument(username, animalId, statusLogId, file, type))
    }

    @GetMapping("/{animalId}/attributes/history")
    suspend fun getAttributesHistory(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<List<AttributeHistoryResponse>> {
        return ResponseEntity.ok(animalService.getAnimalAttributesHistory(animalId))
    }
}