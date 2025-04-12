package ru.animaltracker.userservice.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.*
import ru.animaltracker.userservice.service.interfaces.AnimalService

@RestController
@RequestMapping("/api/animals")
class AnimalController(
    private val animalService: AnimalService
) {

    @PostMapping
     fun createAnimal(
        @RequestHeader("X-User-Name") username: String,
        @Valid @RequestBody request: AnimalCreateRequest
    ): ResponseEntity<AnimalResponse> {
        return ResponseEntity.ok(animalService.createAnimal(username, request))
    }

    @GetMapping("/{animalId}")
     fun getAnimal(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<AnimalResponse> {
        return ResponseEntity.ok(animalService.getAnimal(username, animalId))
    }

    @GetMapping("/{animalId}/status-logs")
     fun getAnimalStatusLogs(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<List<StatusLogResponse>>{
        return ResponseEntity.ok(animalService.getStatusLogs(username, animalId))
    }

    @DeleteMapping("/{animalId}")
     fun deleteAnimal(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<Void> {
        animalService.deleteAnimal(username, animalId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{animalId}/photos")
     fun uploadAnimalPhoto(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @RequestParam file: MultipartFile
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(animalService.addAnimalPhoto(username, animalId, file))
    }

    @PostMapping("/{animalId}/documents")
     fun uploadAnimalDocument(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @RequestParam file: MultipartFile,
        @RequestParam type: String
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(animalService.addAnimalDocument(username, animalId, file, type))
    }

    @PostMapping("/{animalId}/status-logs")
     fun addStatusLog(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @Valid @RequestBody request: StatusLogCreateRequest
    ): ResponseEntity<StatusLogResponse> {
        return ResponseEntity.ok(animalService.addStatusLog(username, animalId, request))
    }

    @PostMapping("/{animalId}/status-logs/{statusLogId}/photos")
     fun uploadStatusLogPhoto(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long,
        @RequestParam file: MultipartFile
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(animalService.addStatusLogPhoto(username, animalId, statusLogId, file))
    }

    @PostMapping("/{animalId}/status-logs/{statusLogId}/documents")
     fun uploadStatusLogDocument(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long,
        @RequestParam file: MultipartFile,
        @RequestParam type: String
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(animalService.addStatusLogDocument(username, animalId, statusLogId, file, type))
    }

    @GetMapping("/{animalId}/attributes/history")
     fun getAttributesHistory(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<List<AttributeHistoryResponse>> {
        return ResponseEntity.ok(animalService.getAnimalAttributesHistory(animalId))
    }

    @PatchMapping("/{animalId}")
     fun updateAnimal(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @Valid @RequestBody request: AnimalUpdateRequest
    ): ResponseEntity<AnimalResponse> {
        return ResponseEntity.ok(animalService.updateAnimal(username, animalId, request))
    }

    @PutMapping("/{animalId}/status-logs/{statusLogId}")
     fun updateStatusLog(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long,
        @Valid @RequestBody request: StatusLogUpdateRequest
    ): ResponseEntity<StatusLogResponse> {
        return ResponseEntity.ok(
            animalService.updateStatusLog(username, animalId, statusLogId, request)
        )
    }

    @DeleteMapping("/{animalId}/status-logs/{statusLogId}")
     fun deleteStatusLog(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long
    ): ResponseEntity<Void> {
        animalService.deleteStatusLog(username, animalId, statusLogId)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{animalId}/attributes/{attributeId}")
     fun updateAttribute(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable attributeId: Short,
        @Valid @RequestBody request: AttributeUpdateRequest
    ): ResponseEntity<AttributeResponse> {
        return ResponseEntity.ok(
            animalService.updateAttribute(username, animalId, attributeId, request)
        )
    }

    @PostMapping("/{animalId}/attributes")
     fun addAttribute(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @Valid @RequestBody request: AttributeRequest
    ): ResponseEntity<AttributeResponse> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(animalService.addAttribute(username, animalId, request))
    }

    @DeleteMapping("/{animalId}/attributes/{attributeId}")
     fun deleteAttribute(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable attributeId: Short
    ): ResponseEntity<Void> {
        animalService.deleteAttribute(username, animalId, attributeId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/photos/{photoId}")
     fun deleteAnimalPhoto(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable photoId: Long
    ): ResponseEntity<Void> {
        animalService.deleteAnimalPhoto(username, photoId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/documents/{documentId}")
     fun deleteAnimalDocument(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable documentId: Long
    ): ResponseEntity<Void> {
        animalService.deleteAnimalDocument(username, documentId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{animalId}/analytics")
     fun getAnimalAnalytics(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<List<AnimalAnalyticsResponse>> {
        return ResponseEntity.ok(animalService.getAnimalAnalytics(animalId))
    }

    @GetMapping("/{animalId}/export/pdf")
     fun exportAnimalToPdf(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<ByteArray> {
        val pdfBytes = animalService.exportAnimalToPdf(username, animalId)

        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=animal_${animalId}.pdf")
            .body(pdfBytes)
    }
}