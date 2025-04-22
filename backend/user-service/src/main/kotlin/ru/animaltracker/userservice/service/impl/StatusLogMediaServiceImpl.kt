package ru.animaltracker.userservice.service.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.S3FileResponse
import ru.animaltracker.userservice.entity.*
import ru.animaltracker.userservice.repository.*
import ru.animaltracker.userservice.service.interfaces.AnimalValidationService
import ru.animaltracker.userservice.service.interfaces.S3Service
import ru.animaltracker.userservice.service.interfaces.StatusLogMediaService

@Service
class StatusLogMediaServiceImpl(
    private val photoRepository: PhotoRepository,
    private val statusLogPhotoRepository: StatusLogPhotoRepository,
    private val documentRepository: DocumentRepository,
    private val statusLogDocumentRepository: StatusLogDocumentRepository,
    private val s3Service: S3Service,
    private val animalValidationService: AnimalValidationService
) : StatusLogMediaService {

    @Transactional
    override fun addStatusLogPhoto(
        username: String,
        animalId: Long,
        statusLogId: Long,
        file: MultipartFile
    ): S3FileResponse {
        val (_, _, statusLog) = animalValidationService.validateUserAndStatusLog(username, animalId, statusLogId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/status-logs/$statusLogId/photos")
        val photo = photoRepository.save(Photo().apply {
            this.objectKey = objectKey
        })

        statusLogPhotoRepository.save(StatusLogPhoto().apply {
            this.animalStatusLog = statusLog
            this.photo = photo
        })

        return S3FileResponse(objectKey, s3Service.generatePresignedUrl(objectKey))
    }

    @Transactional
    override fun addStatusLogDocument(
        username: String,
        animalId: Long,
        statusLogId: Long,
        file: MultipartFile,
        type: String
    ): S3FileResponse {
        val (_, animal, statusLog) = animalValidationService.validateUserAndStatusLog(username, animalId, statusLogId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/status-logs/$statusLogId/documents")
        val document = documentRepository.save(Document().apply {
            this.type = type
            this.objectKey = objectKey
            this.documentName = file.originalFilename
            this.animal = animal
        })

        statusLogDocumentRepository.save(StatusLogDocument().apply {
            this.animalStatusLog = statusLog
            this.document = document
        })

        return S3FileResponse(objectKey, s3Service.generatePresignedUrl(objectKey))
    }
}