package ru.animaltracker.userservice.service.impl

import jakarta.persistence.EntityNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.*
import ru.animaltracker.userservice.entity.*
import ru.animaltracker.userservice.pdfexport.PdfExporter
import ru.animaltracker.userservice.repository.*
import ru.animaltracker.userservice.service.interfaces.AnimalMapper
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
    private val mapper: AnimalMapper
) : AnimalService {

    @Autowired
    private lateinit var pdfExporter: PdfExporter

    override suspend fun exportAnimalToPdf(username: String, animalId: Long): ByteArray {
        val (_, animal) = validateUserAndAnimal(username, animalId)
        return withContext(Dispatchers.IO) {
            pdfExporter.exportAnimal(animal)
        }
    }

    override suspend fun createAnimal(username: String, request: AnimalCreateRequest): AnimalResponse {
        val user = withContext(Dispatchers.IO) {
            userRepository.findByUsername(username)
        }
            ?: throw EntityNotFoundException("User not found")

        val animal = Animal(
            name = request.name,
            description = request.description,
            birthDate = request.birthDate,
            mass = request.mass
        )

        request.attributes.forEach { attrRequest ->
            val attribute = Attribute(
                name = attrRequest.name,
                animal = animal
            )
            attribute.values.add(Value(value = attrRequest.value, attribute = attribute))
            animal.attributes.add(attribute)
        }

        user.addAnimal(animal)
        withContext(Dispatchers.IO) {
            animalRepository.save(animal)
        }

        return mapper.toResponse(animal)
    }

    override suspend fun addAnimalPhoto(username: String, animalId: Long, file: MultipartFile): S3FileResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/photos")
        val photo = withContext(Dispatchers.IO) {
            photoRepository.save(Photo(objectKey = objectKey))
        }

        animal.addPhoto(photo)
        withContext(Dispatchers.IO) {
            animalRepository.save(animal)
        }

        return S3FileResponse(
            objectKey = objectKey,
            presignedUrl = s3Service.generatePresignedUrl(objectKey)
        )
    }

    override suspend fun addAnimalDocument(username: String, animalId: Long, file: MultipartFile, type: String): S3FileResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/documents")
        val document = Document(
            type = type,
            objectKey = objectKey,
            documentName = file.originalFilename,
            animal = animal
        )

        withContext(Dispatchers.IO) {
            documentRepository.save(document)
        }
        animal.addDocument(document)
        withContext(Dispatchers.IO) {
            animalRepository.save(animal)
        }

        return S3FileResponse(
            objectKey = objectKey,
            presignedUrl = s3Service.generatePresignedUrl(objectKey)
        )
    }

    override suspend fun addStatusLog(username: String, animalId: Long, request: StatusLogCreateRequest): StatusLogResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val statusLog = AnimalStatusLog(
            notes = request.notes,
            logDate = request.logDate ?: LocalDate.now(),
            animal = animal,
            user = user
        )

        withContext(Dispatchers.IO) {
            statusLogRepository.save(statusLog)
        }
        animal.addStatusLog(statusLog)
        withContext(Dispatchers.IO) {
            animalRepository.save(animal)
        }

        return mapper.toStatusLogResponse(statusLog)
    }

    override suspend fun addStatusLogPhoto(username: String, animalId: Long, statusLogId: Long, file: MultipartFile): S3FileResponse {
        val (user, animal, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/status-logs/$statusLogId/photos")
        val photo = withContext(Dispatchers.IO) {
            photoRepository.save(Photo(objectKey = objectKey))
        }

        val statusLogPhoto = StatusLogPhoto(statusLog = statusLog, photo = photo)
        statusLog.statusLogPhotos.add(statusLogPhoto)
        withContext(Dispatchers.IO) {
            statusLogRepository.save(statusLog)
        }

        return S3FileResponse(
            objectKey = objectKey,
            presignedUrl = s3Service.generatePresignedUrl(objectKey)
        )
    }

    override suspend fun addStatusLogDocument(username: String, animalId: Long, statusLogId: Long, file: MultipartFile, type: String): S3FileResponse {
        val (user, animal, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

        val objectKey = s3Service.uploadFile(file, "animals/$animalId/status-logs/$statusLogId/documents")
        val document = Document(
            type = type,
            objectKey = objectKey,
            documentName = file.originalFilename,
            animal = animal
        )

        withContext(Dispatchers.IO) {
            documentRepository.save(document)
        }

        val statusLogDocument = StatusLogDocument(statusLog = statusLog, document = document)
        statusLog.statusLogDocuments.add(statusLogDocument)
        withContext(Dispatchers.IO) {
            statusLogRepository.save(statusLog)
        }

        return S3FileResponse(
            objectKey = objectKey,
            presignedUrl = s3Service.generatePresignedUrl(objectKey)
        )
    }

    override suspend fun getAnimalAttributesHistory(animalId: Long): List<AttributeHistoryResponse> {
        val animal = withContext(Dispatchers.IO) {
            animalRepository.findById(animalId)
        }
            .orElseThrow { EntityNotFoundException("Animal not found") }

        return withContext(Dispatchers.IO) {
            attributeRepository.findByAnimalId(animalId)
        }
            .flatMap { attribute ->
                attribute.attributeValueHistories.map { history ->
                    AttributeHistoryResponse(
                        attributeName = attribute.name ?: "",
                        oldValue = history.value,
                        changedAt = history.recordedAt ?: LocalDate.now(),
                        changedBy = history.user.username ?: ""
                    )
                }
            }
    }

    private suspend fun validateUserAndAnimal(username: String, animalId: Long): Pair<User, Animal> {
        val user = withContext(Dispatchers.IO) {
            userRepository.findByUsername(username)
        }
            ?: throw EntityNotFoundException("User not found")

        val animal = withContext(Dispatchers.IO) {
            animalRepository.findById(animalId)
        }
            .orElseThrow { EntityNotFoundException("Animal not found") }

        if (!user.getAnimals().contains(animal)) {
            throw AccessDeniedException("User doesn't have access to this animal")
        }

        return user to animal
    }

    private suspend fun validateUserAndStatusLog(username: String, animalId: Long, statusLogId: Long): Triple<User, Animal, AnimalStatusLog> {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        val statusLog = withContext(Dispatchers.IO) {
            statusLogRepository.findById(statusLogId)
        }
            .orElseThrow { EntityNotFoundException("Status log not found") }

        if (statusLog.animal.id != animalId) {
            throw AccessDeniedException("Status log doesn't belong to this animal")
        }

        return Triple(user, animal, statusLog)
    }

    override suspend fun getUserAnimals(username: String): List<AnimalResponse> {
        val user = withContext(Dispatchers.IO) {
            userRepository.findByUsername(username)
        }
            ?: throw EntityNotFoundException("User not found")

        return user.getAnimals().map { mapper.toResponse(it) }
    }

    override suspend fun getAnimal(username: String, animalId: Long): AnimalResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)
        return mapper.toResponse(animal)
    }

    override suspend fun getStatusLogs(username: String, animalId: Long): List<StatusLogResponse> {
        val (_, animal) = validateUserAndAnimal(username, animalId)
        return animal.statusLogs.map { mapper.toStatusLogResponse(it) }
    }

    @Transactional
    override suspend fun deleteAnimal(username: String, animalId: Long) {
        val (user, animal) = validateUserAndAnimal(username, animalId)

        animal.documents.forEach { s3Service.deleteFile(it.objectKey!!) }
        animal.animalPhotos.forEach { s3Service.deleteFile(it.photo?.objectKey!!) }

        withContext(Dispatchers.IO) {
            animalRepository.delete(animal)
        }
    }

    override suspend fun updateAnimal(username: String, animalId: Long, request: AnimalUpdateRequest): AnimalResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        request.name?.let { animal.name = it }
        request.description?.let { animal.description = it }
        request.birthDate?.let { animal.birthDate = it }
        request.mass?.let { animal.mass = it }

        val updatedAnimal = withContext(Dispatchers.IO) {
            animalRepository.save(animal)
        }

        return mapper.toResponse(updatedAnimal)
    }

    override suspend fun updateStatusLog(
        username: String,
        animalId: Long,
        statusLogId: Long,
        request: StatusLogUpdateRequest
    ): StatusLogResponse {
        val (_, _, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

        request.notes?.let { statusLog.notes = it }
        request.logDate?.let { statusLog.logDate = it }

        val updatedLog = withContext(Dispatchers.IO) {
            statusLogRepository.save(statusLog)
        }

        return mapper.toStatusLogResponse(updatedLog)
    }

    override suspend fun deleteStatusLog(username: String, animalId: Long, statusLogId: Long) {
        val (_, _, statusLog) = validateUserAndStatusLog(username, animalId, statusLogId)

        statusLog.statusLogPhotos.forEach {
            it.photo?.objectKey?.let { key -> s3Service.deleteFile(key) }
        }

        statusLog.statusLogDocuments.forEach {
            it.document?.objectKey?.let { key -> s3Service.deleteFile(key) }
        }

        withContext(Dispatchers.IO) {
            statusLogRepository.delete(statusLog)
        }
    }

    override suspend fun updateAttribute(
        username: String,
        animalId: Long,
        attributeId: Short,
        request: AttributeUpdateRequest
    ): AttributeResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)
        val attribute = withContext(Dispatchers.IO) {
            attributeRepository.findById(attributeId)
        }
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
            attribute.attributeValueHistories.add(history)
        }

        val updatedAttr = withContext(Dispatchers.IO) {
            attributeRepository.save(attribute)
        }

        return mapper.toAttributeResponse(updatedAttr)
    }

    override suspend fun addAttribute(
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

        val savedAttr = withContext(Dispatchers.IO) {
            attributeRepository.save(attribute)
        }

        return mapper.toAttributeResponse(savedAttr)
    }

    override suspend fun deleteAttribute(
        username: String,
        animalId: Long,
        attributeId: Short
    ) {
        val (_, animal) = validateUserAndAnimal(username, animalId)
        val attribute = withContext(Dispatchers.IO) {
            attributeRepository.findById(attributeId)
        }
            .orElseThrow { EntityNotFoundException("Attribute not found") }

        if (attribute.animal?.id != animalId) {
            throw AccessDeniedException("Attribute doesn't belong to this animal")
        }

        withContext(Dispatchers.IO) {
            attributeRepository.delete(attribute)
        }
    }

    override suspend fun deleteAnimalPhoto(username: String, photoId: Long) {
        val photo = withContext(Dispatchers.IO) {
            photoRepository.findById(photoId)
        }.orElseThrow { EntityNotFoundException("Photo not found") }

        val animalPhoto = photo.animalPhotos.firstOrNull()
            ?: throw AccessDeniedException("Photo not linked to animal")

        validateUserAndAnimal(username, animalPhoto.animal?.id ?: throw IllegalStateException())

        withContext(Dispatchers.IO) {
            photo.objectKey?.let { s3Service.deleteFile(it) }
            photoRepository.delete(photo)
        }
    }

    override suspend fun deleteAnimalDocument(username: String, documentId: Long) {
        val document = withContext(Dispatchers.IO) {
            documentRepository.findById(documentId)
        }.orElseThrow { EntityNotFoundException("Document not found") }

        validateUserAndAnimal(username, document.animal?.id ?: throw IllegalStateException())

        withContext(Dispatchers.IO) {
            document.objectKey?.let { s3Service.deleteFile(it) }
            documentRepository.delete(document)
        }
    }

    override suspend fun getAnimalAnalytics(animalId: Long): List<AnimalAnalyticsResponse> {
        val attributes = withContext(Dispatchers.IO) {
            attributeRepository.findByAnimalId(animalId)
        }

        return attributes.map { attribute ->
            val histories = attribute.attributeValueHistories
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