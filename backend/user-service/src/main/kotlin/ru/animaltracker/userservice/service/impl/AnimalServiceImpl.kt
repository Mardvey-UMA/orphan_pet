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
@Transactional
class AnimalServiceImpl(
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val attributeRepository: AttributeRepository,
    private val documentRepository: DocumentRepository,
    private val photoRepository: PhotoRepository,
    private val animalPhotoRepository: AnimalPhotoRepository,
    private val statusLogRepository: AnimalStatusLogRepository,
    private val s3Service: S3Service,
) : AnimalService {

    @Autowired
    private lateinit var pdfExporter: PdfExporter

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

        request.attributes.forEach { attrRequest ->
            val attribute = Attribute().apply {
                name = attrRequest.name
                this.animal = animal
                addValue(attrRequest.value)
            }
            attributeRepository.save(attribute)
        }

        val savedAnimal = animalRepository.save(animal)
        user.addAnimal(savedAnimal)
        userRepository.save(user)

        return savedAnimal.toDto()
    }

    override fun addAnimalPhoto(username: String, animalId: Long, file: MultipartFile): S3FileResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/photos")
        val photo = photoRepository.save(Photo(objectKey = objectKey))

        val animalPhoto = AnimalPhoto(animal = animal, photo = photo)
        animalPhotoRepository.save(animalPhoto)

        return S3FileResponse(objectKey, s3Service.generatePresignedUrl(objectKey))
    }

    override  fun addAnimalDocument(username: String, animalId: Long, file: MultipartFile, type: String): S3FileResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/documents")
        val document = Document(
            type = type,
            objectKey = objectKey,
            documentName = file.originalFilename,
            animal = animal
        )


            documentRepository.save(document)

        animal.addDocument(document)

            animalRepository.save(animal)


        return S3FileResponse(
            objectKey = objectKey,
            presignedUrl = s3Service.generatePresignedUrl(objectKey)
        )
    }

    override fun addStatusLog(username: String, animalId: Long, request: StatusLogCreateRequest): StatusLogResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val statusLog = AnimalStatusLog(
            notes = request.notes,
            logDate = request.logDate ?: LocalDate.now(),
            animal = animal,
            user = user
        )

        statusLogRepository.save(statusLog)
        animal.addStatusLog(statusLog)
        animalRepository.save(animal)

        return statusLog.toDto()
    }

    override  fun addStatusLogPhoto(username: String, animalId: Long, statusLogId: Long, file: MultipartFile): S3FileResponse {
        val (user, animal, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/status-logs/$statusLogId/photos")
        val photo = photoRepository.save(Photo(objectKey = objectKey))


        val statusLogPhoto = StatusLogPhoto(statusLog = statusLog, photo = photo)
        statusLog.statusLogPhotos.add(statusLogPhoto)

            statusLogRepository.save(statusLog)

        return S3FileResponse(
            objectKey = objectKey,
            presignedUrl = s3Service.generatePresignedUrl(objectKey)
        )
    }

    override  fun addStatusLogDocument(username: String, animalId: Long, statusLogId: Long, file: MultipartFile, type: String): S3FileResponse {
        val (user, animal, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/status-logs/$statusLogId/documents")
        val document = Document(
            type = type,
            objectKey = objectKey,
            documentName = file.originalFilename,
            animal = animal
        )


            documentRepository.save(document)


        val statusLogDocument = StatusLogDocument(statusLog = statusLog, document = document)
        statusLog.statusLogDocuments.add(statusLogDocument)

            statusLogRepository.save(statusLog)


        return S3FileResponse(
            objectKey = objectKey,
            presignedUrl = s3Service.generatePresignedUrl(objectKey)
        )
    }

    override  fun getAnimalAttributesHistory(animalId: Long): List<AttributeHistoryResponse> {
        val animal =
            animalRepository.findById(animalId)

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

    @Transactional
     fun validateUserAndAnimal(username: String, animalId: Long): Pair<User, Animal> {
        val user =
            userRepository.findByUsername(username)

            ?: throw EntityNotFoundException("User not found")

        val animal =
            animalRepository.findById(animalId)

            .orElseThrow { EntityNotFoundException("Animal not found") }

        if (!user.getAnimals().contains(animal)) {
            throw AccessDeniedException("User doesn't have access to this animal")
        }

        return user to animal
    }

    @Transactional
     fun validateUserAndStatusLog(username: String, animalId: Long, statusLogId: Long): Triple<User, Animal, AnimalStatusLog> {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val statusLog =
            statusLogRepository.findById(statusLogId)

            .orElseThrow { EntityNotFoundException("Status log not found") }

        if (statusLog.animal.id != animalId) {
            throw AccessDeniedException("Status log doesn't belong to this animal")
        }

        return Triple(user, animal, statusLog)
    }

    override  fun getUserAnimals(username: String): List<AnimalResponse> {
        val user =
            userRepository.findByUsername(username)

            ?: throw EntityNotFoundException("User not found")

        return user.getAnimals().map { it.toDto() }
    }

    override  fun getAnimal(username: String, animalId: Long): AnimalResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)
        return animal.toDto()
    }

    override  fun getStatusLog(id: Long): StatusLogResponse {
        val log =
            statusLogRepository.findById(id)
        .orElseThrow { EntityNotFoundException("Status log not found") }
        return log.toDto()
    }

    override  fun getStatusLogs(username: String, animalId: Long): List<StatusLogResponse> {
        val (_, animal) = validateUserAndAnimal(username, animalId)
        return animal.statusLogs.map { it.toDto() }
    }

    @Transactional
    override  fun deleteAnimal(username: String, animalId: Long) {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        animal.documents.forEach { s3Service.deleteFile(it.objectKey!!) }
        animal.animalPhotos.forEach { s3Service.deleteFile(it.photo?.objectKey!!) }


            animalRepository.delete(animal)

    }

    override  fun updateAnimal(username: String, animalId: Long, request: AnimalUpdateRequest): AnimalResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        request.name?.let { animal.name = it }
        request.description?.let { animal.description = it }
        request.birthDate?.let { animal.birthDate = it }
        request.mass?.let { animal.mass = it }

        val updatedAnimal =
            animalRepository.save(animal)


        return updatedAnimal.toDto()
    }

    override  fun updateStatusLog(
        username: String,
        animalId: Long,
        statusLogId: Long,
        request: StatusLogUpdateRequest
    ): StatusLogResponse {
        val (_, _, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

        request.notes?.let { statusLog.notes = it }
        request.logDate?.let { statusLog.logDate = it }

        val updatedLog =
            statusLogRepository.save(statusLog)


        return updatedLog.toDto()
    }

    override  fun deleteStatusLog(username: String, animalId: Long, statusLogId: Long) {
        val (_, _, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

        statusLog.statusLogPhotos.forEach {
            it.photo?.objectKey?.let { key -> s3Service.deleteFile(key) }
        }

        statusLog.statusLogDocuments.forEach {
            it.document?.objectKey?.let { key -> s3Service.deleteFile(key) }
        }


            statusLogRepository.delete(statusLog)

    }

    override  fun updateAttribute(
        username: String,
        animalId: Long,
        attributeId: Short,
        request: AttributeUpdateRequest
    ): AttributeResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)
        val attribute =
            attributeRepository.findById(attributeId)

            .orElseThrow { EntityNotFoundException("Attribute not found") }

        if (attribute.animal?.id != animalId) {
            throw AccessDeniedException("Attribute doesn't belong to this animal")
        }

        request.name?.let { attribute.name = it }
        if (request.value != null) {
            val value = attribute.values.firstOrNull() ?: Value(attribute = attribute)
            value.value = request.value
            attribute.values.clear()
            attribute.values.add(value)

            val history = AttributeValueHistory(
                value = request.value,
                recordedAt = LocalDate.now(),
                user = user,
                animal = animal,
                attribute = attribute
            )
            attribute.valueHistories.add(history)
        }

        val updatedAttr =
            attributeRepository.save(attribute)


        return updatedAttr.toDto()
    }

    override  fun addAttribute(
        username: String,
        animalId: Long,
        request: AttributeRequest
    ): AttributeResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val attribute = Attribute(
            name = request.name,
            animal = animal
        ).apply {
            values.add(Value(value = request.value, attribute = this))
        }

        val savedAttr =
            attributeRepository.save(attribute)


        return savedAttr.toDto()
    }

    override  fun deleteAttribute(
        username: String,
        animalId: Long,
        attributeId: Short
    ) {
        val (_, animal) = validateUserAndAnimal(username, animalId)
        val attribute =
            attributeRepository.findById(attributeId)

            .orElseThrow { EntityNotFoundException("Attribute not found") }

        if (attribute.animal?.id != animalId) {
            throw AccessDeniedException("Attribute doesn't belong to this animal")
        }


            attributeRepository.delete(attribute)

    }

    override  fun deleteAnimalPhoto(username: String, photoId: Long) {
        val photo =
            photoRepository.findById(photoId)
        .orElseThrow { EntityNotFoundException("Photo not found") }

        val animalPhoto = photo.animalPhotos.firstOrNull()
            ?: throw AccessDeniedException("Photo not linked to animal")

        validateUserAndAnimal(username, animalPhoto.animal?.id ?: throw IllegalStateException())


            photo.objectKey?.let { s3Service.deleteFile(it) }
            photoRepository.delete(photo)

    }

    override  fun deleteAnimalDocument(username: String, documentId: Long) {
        val document =
            documentRepository.findById(documentId)
        .orElseThrow { EntityNotFoundException("Document not found") }

        validateUserAndAnimal(username, document.animal?.id ?: throw IllegalStateException())


            document.objectKey?.let { s3Service.deleteFile(it) }
            documentRepository.delete(document)

    }

    override  fun getAnimalAnalytics(animalId: Long): List<AnimalAnalyticsResponse> {
        val attributes =
            attributeRepository.findByAnimalId(animalId)


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