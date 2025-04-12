package ru.animaltracker.userservice.service.interfaces

import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.*

interface AnimalService {
    fun createAnimal(username: String, request: AnimalCreateRequest): AnimalResponse
     fun  addAnimalPhoto(username: String, animalId: Long, file: MultipartFile): S3FileResponse
     fun addAnimalDocument(username: String, animalId: Long, file: MultipartFile, type: String): S3FileResponse
     fun addStatusLog(username: String, animalId: Long, request: StatusLogCreateRequest): StatusLogResponse
     fun addStatusLogPhoto(username: String, animalId: Long, statusLogId: Long, file: MultipartFile): S3FileResponse
     fun addStatusLogDocument(username: String, animalId: Long, statusLogId: Long, file: MultipartFile, type: String): S3FileResponse
     fun getAnimalAttributesHistory(animalId: Long): List<AttributeHistoryResponse>
     fun getUserAnimals(username: String): List<AnimalResponse>
     fun getAnimal(username: String, animalId: Long): AnimalResponse
     fun getStatusLogs(username: String, animalId: Long): List<StatusLogResponse>
     fun deleteAnimal(username: String, animalId: Long)
     fun updateAnimal(username: String, animalId: Long, request: AnimalUpdateRequest): AnimalResponse
     fun updateStatusLog(username: String, animalId: Long, statusLogId: Long, request: StatusLogUpdateRequest): StatusLogResponse
     fun deleteStatusLog(username: String, animalId: Long, statusLogId: Long)
     fun updateAttribute(username: String, animalId: Long, attributeId: Short, request: AttributeUpdateRequest): AttributeResponse
     fun addAttribute(username: String, animalId: Long, request: AttributeRequest): AttributeResponse
     fun deleteAttribute(username: String, animalId: Long, attributeId: Short)
     fun deleteAnimalPhoto(username: String, photoId: Long)
     fun deleteAnimalDocument(username: String, documentId: Long)
     fun getAnimalAnalytics(animalId: Long): List<AnimalAnalyticsResponse>
     fun exportAnimalToPdf(username: String, animalId: Long): ByteArray
     fun getStatusLog(id: Long): StatusLogResponse
}

