package ru.animaltracker.userservice.service.interfaces

import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.S3FileResponse

interface StatusLogMediaService {
    fun addStatusLogPhoto(username: String, animalId: Long, statusLogId: Long, file: MultipartFile): S3FileResponse
    fun addStatusLogDocument(username: String, animalId: Long, statusLogId: Long, file: MultipartFile, type: String): S3FileResponse
}