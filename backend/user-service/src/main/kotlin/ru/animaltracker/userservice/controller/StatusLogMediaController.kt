package ru.animaltracker.userservice.controller
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.*
import ru.animaltracker.userservice.service.interfaces.StatusLogMediaService
import org.springframework.http.MediaType

@RestController
@RequestMapping("/api/animals/{animalId}/status-logs/{statusLogId}/media")
class StatusLogMediaController(
    private val statusLogMediaService: StatusLogMediaService
) {

    @PostMapping("/photos", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadStatusLogPhoto(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(
            statusLogMediaService.addStatusLogPhoto(username, animalId, statusLogId, file)
        )
    }

    @PostMapping("/documents", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadStatusLogDocument(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long,
        @PathVariable statusLogId: Long,
        @RequestParam("file") file: MultipartFile,
        @RequestParam type: String
    ): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(
            statusLogMediaService.addStatusLogDocument(username, animalId, statusLogId, file, type)
        )
    }
}