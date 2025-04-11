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
    suspend fun updateAnimal(username: String, animalId: Long, request: AnimalUpdateRequest): AnimalResponse
    suspend fun updateStatusLog(username: String, animalId: Long, statusLogId: Long, request: StatusLogUpdateRequest): StatusLogResponse
    suspend fun deleteStatusLog(username: String, animalId: Long, statusLogId: Long)
    suspend fun updateAttribute(username: String, animalId: Long, attributeId: Short, request: AttributeUpdateRequest): AttributeResponse
    suspend fun addAttribute(username: String, animalId: Long, request: AttributeRequest): AttributeResponse
    suspend fun deleteAttribute(username: String, animalId: Long, attributeId: Short)
    suspend fun deleteAnimalPhoto(username: String, photoId: Long)
    suspend fun deleteAnimalDocument(username: String, documentId: Long)
    suspend fun getAnimalAnalytics(animalId: Long): List<AnimalAnalyticsResponse>
    suspend fun exportAnimalToPdf(username: String, animalId: Long): ByteArray
    suspend fun getStatusLog(id: Long): StatusLogResponse
}

