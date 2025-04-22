package ru.animaltracker.userservice.controller

import ru.animaltracker.userservice.service.interfaces.AnimalMediaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.*

@RestController
@RequestMapping("/api/animals/{animalId}/media")
class AnimalMediaController(
    private val animalMediaService: AnimalMediaService
) {

    @PostMapping("/photos", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadAnimalPhoto(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(animalMediaService.addAnimalPhoto(username, animalId, file))
    }

    @PostMapping("/documents", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadAnimalDocument(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @RequestParam("file") file: MultipartFile,
        @RequestParam type: String
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(animalMediaService.addAnimalDocument(username, animalId, file, type))
    }

    @DeleteMapping("/photos/{photoId}")
    fun deleteAnimalPhoto(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable photoId: Long
    ): ResponseEntity<Void> {
        animalMediaService.deleteAnimalPhoto(username, photoId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/documents/{documentId}")
    fun deleteAnimalDocument(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable documentId: Long
    ): ResponseEntity<Void> {
        animalMediaService.deleteAnimalDocument(username, documentId)
        return ResponseEntity.noContent().build()
    }
}