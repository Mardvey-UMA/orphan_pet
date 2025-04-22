package ru.animaltracker.userservice.service.impl

import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.S3FileResponse
import ru.animaltracker.userservice.service.interfaces.AnimalMediaService

class AnimalMediaServiceImpl : AnimalMediaService {
    override fun addAnimalPhoto(username: String, animalId: Long, file: MultipartFile): S3FileResponse {
        TODO("Not yet implemented")
    }

    override fun addAnimalDocument(
        username: String,
        animalId: Long,
        file: MultipartFile,
        type: String
    ): S3FileResponse {
        TODO("Not yet implemented")
    }

    override fun deleteAnimalPhoto(username: String, photoId: Long) {
        TODO("Not yet implemented")
    }

    override fun deleteAnimalDocument(username: String, documentId: Long) {
        TODO("Not yet implemented")
    }
}