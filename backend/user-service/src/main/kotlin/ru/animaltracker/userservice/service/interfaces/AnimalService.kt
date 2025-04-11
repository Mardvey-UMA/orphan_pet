package ru.animaltracker.userservice.service.interfaces

import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.*

interface AnimalService {
    suspend fun createAnimal(username: String, request: AnimalCreateRequest): AnimalResponse
    suspend fun addAnimalPhoto(username: String, animalId: Long, file: MultipartFile): S3FileResponse
    suspend fun addAnimalDocument(username: String, animalId: Long, file: MultipartFile, type: String): S3FileResponse
    suspend fun addStatusLog(username: String, animalId: Long, request: StatusLogCreateRequest): StatusLogResponse
    suspend fun addStatusLogPhoto(username: String, animalId: Long, statusLogId: Long, file: MultipartFile): S3FileResponse
    suspend fun addStatusLogDocument(username: String, animalId: Long, statusLogId: Long, file: MultipartFile, type: String): S3FileResponse
    suspend fun getAnimalAttributesHistory(animalId: Long): List<AttributeHistoryResponse>
    suspend fun getUserAnimals(username: String): List<AnimalResponse>
    suspend fun getAnimal(username: String, animalId: Long): AnimalResponse
    suspend fun getStatusLogs(username: String, animalId: Long): List<StatusLogResponse>
    suspend fun deleteAnimal(username: String, animalId: Long)
}