package ru.animaltracker.userservice.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.animaltracker.userservice.dto.*
import ru.animaltracker.userservice.service.interfaces.AnimalManagementService

@RestController
@RequestMapping("/api/animals")
class AnimalManagementController(
    private val animalManagementService: AnimalManagementService
) {

    @PostMapping
    fun createAnimal(
        @RequestHeader("X-User-Name") username: String,
        @Valid @RequestBody request: AnimalCreateRequest
    ): ResponseEntity<AnimalResponse> {
        return ResponseEntity.ok(animalManagementService.createAnimal(username, request))
    }

    @GetMapping("/{animalId}")
    fun getAnimal(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<AnimalResponse> {
        return ResponseEntity.ok(animalManagementService.getAnimal(username, animalId))
    }

    @GetMapping
    fun getUserAnimals(
        @RequestHeader("X-User-Name") username: String
    ): ResponseEntity<List<AnimalResponse>> {
        return ResponseEntity.ok(animalManagementService.getUserAnimals(username))
    }

    @DeleteMapping("/{animalId}")
    fun deleteAnimal(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<Void> {
        animalManagementService.deleteAnimal(username, animalId)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{animalId}")
    fun updateAnimal(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @Valid @RequestBody request: AnimalUpdateRequest
    ): ResponseEntity<AnimalResponse> {
        return ResponseEntity.ok(animalManagementService.updateAnimal(username, animalId, request))
    }

    @PostMapping("/{animalId}/attributes")
    fun addAttribute(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @Valid @RequestBody request: AttributeRequest
    ): ResponseEntity<AttributeResponse> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(animalManagementService.addAttribute(username, animalId, request))
    }

    @PatchMapping("/{animalId}/attributes/{attributeId}")
    fun updateAttribute(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable attributeId: Short,
        @Valid @RequestBody request: AttributeUpdateRequest
    ): ResponseEntity<AttributeResponse> {
        return ResponseEntity.ok(
            animalManagementService.updateAttribute(username, animalId, attributeId, request)
        )
    }

    @DeleteMapping("/{animalId}/attributes/{attributeId}")
    fun deleteAttribute(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable attributeId: Short
    ): ResponseEntity<Void> {
        animalManagementService.deleteAttribute(username, animalId, attributeId)
        return ResponseEntity.noContent().build()
    }
}