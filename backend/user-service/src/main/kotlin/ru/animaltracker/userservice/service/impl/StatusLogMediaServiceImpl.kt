package ru.animaltracker.userservice.service.impl

import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.S3FileResponse
import ru.animaltracker.userservice.service.interfaces.StatusLogMediaService

class StatusLogMediaServiceImpl : StatusLogMediaService {
    override fun addStatusLogPhoto(
        username: String,
        animalId: Long,
        statusLogId: Long,
        file: MultipartFile
    ): S3FileResponse {
        TODO("Not yet implemented")
    }

    override fun addStatusLogDocument(
        username: String,
        animalId: Long,
        statusLogId: Long,
        file: MultipartFile,
        type: String
    ): S3FileResponse {
        TODO("Not yet implemented")
    }
}