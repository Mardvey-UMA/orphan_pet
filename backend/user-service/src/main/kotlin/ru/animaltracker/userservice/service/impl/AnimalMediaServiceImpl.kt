package ru.animaltracker.userservice.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.S3FileResponse
import ru.animaltracker.userservice.entity.*
import ru.animaltracker.userservice.repository.*
import ru.animaltracker.userservice.service.interfaces.AnimalMediaService
import ru.animaltracker.userservice.service.interfaces.AnimalValidationService
import ru.animaltracker.userservice.service.interfaces.S3Service
import java.nio.file.AccessDeniedException

@Service
class AnimalMediaServiceImpl(
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val documentRepository: DocumentRepository,
    private val photoRepository: PhotoRepository,
    private val animalPhotoRepository: AnimalPhotoRepository,
    private val s3Service: S3Service,
    private val animalValidationService: AnimalValidationService
) : AnimalMediaService {

    @Transactional
    override fun addAnimalPhoto(username: String, animalId: Long, file: MultipartFile): S3FileResponse {
        val (_, animal) = animalValidationService.validateUserAndAnimal(username, animalId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/photos")
        val photo = photoRepository.save(Photo().apply {
            this.objectKey = objectKey
        })

        animalPhotoRepository.save(AnimalPhoto().apply {
            this.animal = animal
            this.photo = photo
        })

        return S3FileResponse(objectKey, s3Service.generatePresignedUrl(objectKey))
    }

    @Transactional
    override fun addAnimalDocument(username: String, animalId: Long, file: MultipartFile, type: String): S3FileResponse {
        val (_, animal) = animalValidationService.validateUserAndAnimal(username, animalId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/documents")
        val document = documentRepository.save(Document().apply {
            this.type = type
            this.objectKey = objectKey
            this.documentName = file.originalFilename
            this.animal = animal
        })

        return S3FileResponse(objectKey, s3Service.generatePresignedUrl(objectKey))
    }

    @Transactional
    override fun deleteAnimalPhoto(username: String, photoId: Long) {
        val photo = photoRepository.findById(photoId)
            .orElseThrow { EntityNotFoundException("Photo not found") }

        val animalPhoto = animalPhotoRepository.findByPhotoId(photoId)
            ?: throw AccessDeniedException("Photo not linked to animal")

        animalValidationService.validateUserAndAnimal(username, animalPhoto.animal?.id ?: throw IllegalStateException())

        photo.objectKey?.let { s3Service.deleteFile(it) }
        photoRepository.delete(photo)
    }

    @Transactional
    override fun deleteAnimalDocument(username: String, documentId: Long) {
        val document = documentRepository.findById(documentId)
            .orElseThrow { EntityNotFoundException("Document not found") }

        animalValidationService.validateUserAndAnimal(username, document.animal?.id ?: throw IllegalStateException())

        document.objectKey?.let { s3Service.deleteFile(it) }
        documentRepository.delete(document)
    }

}