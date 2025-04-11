package ru.doedating.userservice.service.interfaces

import org.springframework.web.multipart.MultipartFile
import ru.doedating.userservice.dto.*
import ru.doedating.userservice.entity.Animal
import ru.doedating.userservice.entity.AnimalStatusLog

interface AnimalService {
    fun createAnimal(username: String, animalDto: AnimalDto): Animal
    fun getAnimalsByUser(username: String): List<Animal>
    fun uploadAnimalPhoto(username: String, animalId: Long, file: MultipartFile): FileResponseDto
    fun getAnimalPhotos(username: String, animalId: Long): List<FileResponseDto>
    fun uploadAnimalDocument(username: String, animalId: Long, file: MultipartFile): FileResponseDto
    fun getAnimalDocuments(username: String, animalId: Long): List<FileResponseDto>
    fun addStatusLog(username: String, statusLogDto: AnimalStatusLogDto): AnimalStatusLog
    fun uploadStatusLogPhoto(username: String, statusLogId: Long, file: MultipartFile): FileResponseDto
    fun uploadStatusLogDocument(username: String, statusLogId: Long, file: MultipartFile): FileResponseDto
    fun getAnimalAttributeHistory(animalId: Long): Map<String, List<AttributeHistoryDto>>
}