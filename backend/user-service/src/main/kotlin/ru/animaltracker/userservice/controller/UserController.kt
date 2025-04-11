package ru.animaltracker.userservice.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.AnimalResponse
import ru.animaltracker.userservice.dto.S3FileResponse
import ru.animaltracker.userservice.dto.UserResponse
import ru.animaltracker.userservice.dto.UserUpdateRequest
import ru.animaltracker.userservice.service.interfaces.AnimalService
import ru.animaltracker.userservice.service.interfaces.UserService

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val animalService: AnimalService
) {

    @GetMapping("/me")
    suspend fun getCurrentUser(
        @RequestHeader("X-User-Name") username: String
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.getUser(username))
    }

    @PatchMapping("/me")
    suspend fun updateUser(
        @RequestHeader("X-User-Name") username: String,
        @Valid @RequestBody request: UserUpdateRequest
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.updateUser(username, request))
    }

    @PostMapping("/me/photo")
    suspend fun uploadUserPhoto(
        @RequestHeader("X-User-Name") username: String,
        @RequestParam file: MultipartFile
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(userService.uploadUserPhoto(username, file))
    }

    @GetMapping("/me/animals")
    suspend fun getUserAnimals(
        @RequestHeader("X-User-Name") username: String
    ): ResponseEntity<List<AnimalResponse>> {
        return ResponseEntity.ok(animalService.getUserAnimals(username))
    }
}