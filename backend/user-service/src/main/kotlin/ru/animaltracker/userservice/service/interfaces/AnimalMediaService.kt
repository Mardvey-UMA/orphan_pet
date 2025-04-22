package ru.animaltracker.userservice.service.interfaces

import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.S3FileResponse

interface AnimalMediaService {
    fun addAnimalPhoto(username: String, animalId: Long, file: MultipartFile): S3FileResponse
    fun addAnimalDocument(username: String, animalId: Long, file: MultipartFile, type: String): S3FileResponse
    fun deleteAnimalPhoto(username: String, photoId: Long)
    fun deleteAnimalDocument(username: String, documentId: Long)
}