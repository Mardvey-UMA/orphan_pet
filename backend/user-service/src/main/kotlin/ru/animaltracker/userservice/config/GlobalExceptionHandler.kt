package ru.animaltracker.userservice.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.animaltracker.userservice.exception.FileProcessingException
import ru.animaltracker.userservice.exception.FileUploadException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(FileUploadException::class)
    fun handleFileUploadException(ex: FileUploadException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to ex.message!!))
    }

    @ExceptionHandler(FileProcessingException::class)
    fun handleFileProcessingException(ex: FileProcessingException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to ex.message!!))
    }
}