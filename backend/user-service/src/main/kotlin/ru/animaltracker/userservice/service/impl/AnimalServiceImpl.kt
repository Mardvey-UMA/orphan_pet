package ru.animaltracker.userservice.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.*
import ru.animaltracker.userservice.entity.*
import ru.animaltracker.userservice.pdfexport.PdfExporter
import ru.animaltracker.userservice.repository.*
import ru.animaltracker.userservice.service.interfaces.AnimalService
import ru.animaltracker.userservice.service.interfaces.S3Service
import java.nio.file.AccessDeniedException
import java.time.LocalDate


@Service
class AnimalServiceImpl(
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val attributeRepository: AttributeRepository,
    private val documentRepository: DocumentRepository,
    private val photoRepository: PhotoRepository,
    private val animalPhotoRepository: AnimalPhotoRepository,
    private val statusLogRepository: AnimalStatusLogRepository,
    private val statusLogPhotoRepository: StatusLogPhotoRepository,
    private val statusLogDocumentRepository: StatusLogDocumentRepository,
    private val attributeValueHistoryRepository: AttributeValueHistoryRepository,
    private val s3Service: S3Service,
) : AnimalService {

    @Autowired
    private lateinit var pdfExporter: PdfExporter

    @Transactional(readOnly = true)
    override  fun exportAnimalToPdf(username: String, animalId: Long): ByteArray {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        return pdfExporter.exportAnimal(animal)
    }

    @Transactional
    override fun createAnimal(username: String, request: AnimalCreateRequest): AnimalResponse {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        val animal = Animal().apply {
            name = request.name
            description = request.description
            birthDate = request.birthDate
            mass = request.mass
        }

        val savedAnimal = animalRepository.save(animal)

        request.attributes.forEach { attrRequest ->
            val attribute = Attribute().apply {
                name = attrRequest.name
                this.animal = savedAnimal
                addValue(attrRequest.value)
            }
            attributeRepository.save(attribute)
        }

        user.addAnimal(savedAnimal)
        userRepository.save(user)

        return savedAnimal.toDto(s3Service)
    }

    @Transactional
    override fun addAnimalPhoto(username: String, animalId: Long, file: MultipartFile): S3FileResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)

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
        val (_, animal) = validateUserAndAnimal(username, animalId)

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
    override fun addStatusLog(username: String, animalId: Long, request: StatusLogCreateRequest): StatusLogResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val statusLog = statusLogRepository.save(AnimalStatusLog().apply {
            notes = request.notes
            logDate = request.logDate ?: LocalDate.now()
            this.animal = animal
            this.user = user
        })

        return statusLog.toDto(s3Service)
    }

    @Transactional
    override fun addStatusLogPhoto(username: String, animalId: Long, statusLogId: Long, file: MultipartFile): S3FileResponse {
        val (_, _, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

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
    override fun addStatusLogDocument(username: String, animalId: Long, statusLogId: Long, file: MultipartFile, type: String): S3FileResponse {
        val (_, animal, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

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
    @Transactional(readOnly = true)
    override fun getAnimalAttributesHistory(animalId: Long): List<AttributeHistoryResponse> {
        val animal = animalRepository.findById(animalId)
            .orElseThrow { EntityNotFoundException("Animal not found") }

        return attributeRepository.findByAnimalId(animalId)
            .flatMap { attribute ->
                attribute.valueHistories.map { history ->
                    AttributeHistoryResponse(
                        attributeName = attribute.name ?: "",
                        oldValue = history.value,
                        changedAt = history.recordedAt ?: LocalDate.now(),
                        changedBy = history.user.username ?: ""
                    )
                }
            }
    }

    @Transactional(readOnly = true)
    fun validateUserAndAnimal(username: String, animalId: Long): Pair<User, Animal> {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        val animal = animalRepository.findById(animalId)
            .orElseThrow { EntityNotFoundException("Animal not found") }

        if (animal.animalUsers.none { it.user?.id == user.id }) {
            throw AccessDeniedException("User doesn't have access to this animal")
        }

        return user to animal
    }

    @Transactional(readOnly = true)
    fun validateUserAndStatusLog(username: String, animalId: Long, statusLogId: Long): Triple<User, Animal, AnimalStatusLog> {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val statusLog = statusLogRepository.findById(statusLogId)
            .orElseThrow { EntityNotFoundException("Status log not found") }

        if (statusLog.animal.id != animalId) {
            throw AccessDeniedException("Status log doesn't belong to this animal")
        }

        return Triple(user, animal, statusLog)
    }
    @Transactional(readOnly = true)
    override fun getUserAnimals(username: String): List<AnimalResponse> {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        return user.animalUsers.mapNotNull { it.animal?.toDto(s3Service) }
    }
    @Transactional(readOnly = true)
    override fun getAnimal(username: String, animalId: Long): AnimalResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        return animal.toDto(s3Service)
    }
    @Transactional(readOnly = true)
    override fun getStatusLog(id: Long): StatusLogResponse {
        val log = statusLogRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Status log not found") }
        return log.toDto(s3Service)
    }
    @Transactional(readOnly = true)
    override fun getStatusLogs(username: String, animalId: Long): List<StatusLogResponse> {
        val (_, animal) = validateUserAndAnimal(username, animalId)
        return statusLogRepository.findByAnimalId(animalId).map { it.toDto(s3Service) }
    }

    @Transactional
    override fun deleteAnimal(username: String, animalId: Long) {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        animal.documents.forEach {
            it.objectKey?.let { key -> s3Service.deleteFile(key) }
            documentRepository.delete(it)
        }

        animal.animalPhotos.forEach {
            it.photo?.objectKey?.let { key -> s3Service.deleteFile(key) }
            it.photo?.let { photo -> photoRepository.delete(photo) }
        }

        animalRepository.delete(animal)
    }

    @Transactional
    override fun updateAnimal(username: String, animalId: Long, request: AnimalUpdateRequest): AnimalResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        request.name?.let { animal.name = it }
        request.description?.let { animal.description = it }
        request.birthDate?.let { animal.birthDate = it }
        request.mass?.let { animal.mass = it }

        return animalRepository.save(animal).toDto(s3Service)
    }

    @Transactional
    override fun updateStatusLog(
        username: String,
        animalId: Long,
        statusLogId: Long,
        request: StatusLogUpdateRequest
    ): StatusLogResponse {
        val (_, _, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

        request.notes?.let { statusLog.notes = it }
        request.logDate?.let { statusLog.logDate = it }

        return statusLogRepository.save(statusLog).toDto(s3Service)
    }

    @Transactional
    override fun deleteStatusLog(username: String, animalId: Long, statusLogId: Long) {
        val (_, _, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

        statusLog.statusLogPhotos.forEach {
            it.photo?.objectKey?.let { key -> s3Service.deleteFile(key) }
            it.photo?.let { photo -> photoRepository.delete(photo) }
        }

        statusLog.statusLogDocuments.forEach {
            it.document?.objectKey?.let { key -> s3Service.deleteFile(key) }
            it.document?.let { doc -> documentRepository.delete(doc) }
        }

        statusLogRepository.delete(statusLog)
    }

    @Transactional
    override fun updateAttribute(
        username: String,
        animalId: Long,
        attributeId: Short,
        request: AttributeUpdateRequest
    ): AttributeResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)
        val attribute = attributeRepository.findById(attributeId)
            .orElseThrow { EntityNotFoundException("Attribute not found") }

        if (attribute.animal?.id != animalId) {
            throw AccessDeniedException("Attribute doesn't belong to this animal")
        }

        request.name?.let { attribute.name = it }

        if (request.value != null) {
            val value = attribute.values.firstOrNull() ?: Value().apply {
                this.attribute = attribute
            }
            value.value = request.value
            attribute.values.clear()
            attribute.values.add(value)

            attributeValueHistoryRepository.save(AttributeValueHistory().apply {
                this.value = request.value
                this.recordedAt = LocalDate.now()
                this.user = user
                this.animal = animal
                this.attribute = attribute
            })
        }

        return attributeRepository.save(attribute).toDto()
    }

    @Transactional
    override fun addAttribute(
        username: String,
        animalId: Long,
        request: AttributeRequest
    ): AttributeResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        val attribute = Attribute().apply {
            name = request.name
            this.animal = animal
        }.also { attr ->
            attr.values.add(Value().apply {
                value = request.value
                attribute = attr
            })
        }

        return attributeRepository.save(attribute).toDto()
    }

    @Transactional
    override fun deleteAttribute(username: String, animalId: Long, attributeId: Short) {
        val (_, _) = validateUserAndAnimal(username, animalId)
        val attribute = attributeRepository.findById(attributeId)
            .orElseThrow { EntityNotFoundException("Attribute not found") }

        if (attribute.animal?.id != animalId) {
            throw AccessDeniedException("Attribute doesn't belong to this animal")
        }

        attributeRepository.delete(attribute)
    }

    @Transactional
    override fun deleteAnimalPhoto(username: String, photoId: Long) {
        val photo = photoRepository.findById(photoId)
            .orElseThrow { EntityNotFoundException("Photo not found") }

        val animalPhoto = animalPhotoRepository.findByPhotoId(photoId)
            ?: throw AccessDeniedException("Photo not linked to animal")

        validateUserAndAnimal(username, animalPhoto.animal?.id ?: throw IllegalStateException())

        photo.objectKey?.let { s3Service.deleteFile(it) }
        photoRepository.delete(photo)
    }

    @Transactional
    override fun deleteAnimalDocument(username: String, documentId: Long) {
        val document = documentRepository.findById(documentId)
            .orElseThrow { EntityNotFoundException("Document not found") }

        validateUserAndAnimal(username, document.animal?.id ?: throw IllegalStateException())

        document.objectKey?.let { s3Service.deleteFile(it) }
        documentRepository.delete(document)
    }
    @Transactional(readOnly = true)
    override fun getAnimalAnalytics(animalId: Long): List<AnimalAnalyticsResponse> {
        val attributes = attributeRepository.findByAnimalId(animalId)

        return attributes.map { attribute ->
            val histories = attribute.valueHistories
                .sortedBy { it.recordedAt }
                .map { history ->
                    AttributeChange(
                        date = history.recordedAt ?: LocalDate.now(),
                        value = history.value ?: "",
                        changedBy = history.user.username ?: ""
                    )
                }

            val numericValues = histories
                .mapNotNull { it.value.toDoubleOrNull() }

            val stats = if (numericValues.isNotEmpty()) {
                AttributeStats(
                    minValue = numericValues.min().toString(),
                    maxValue = numericValues.max().toString(),
                    avgValue = numericValues.average()
                )
            } else {
                null
            }

            AnimalAnalyticsResponse(
                attributeName = attribute.name ?: "Unknown",
                changes = histories,
                stats = stats
            )
        }
    }
}