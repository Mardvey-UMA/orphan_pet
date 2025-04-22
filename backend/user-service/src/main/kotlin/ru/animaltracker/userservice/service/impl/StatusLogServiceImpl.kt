package ru.animaltracker.userservice.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.animaltracker.userservice.dto.*
import ru.animaltracker.userservice.entity.*
import ru.animaltracker.userservice.repository.*
import ru.animaltracker.userservice.service.interfaces.AnimalValidationService
import ru.animaltracker.userservice.service.interfaces.S3Service
import ru.animaltracker.userservice.service.interfaces.StatusLogService
import java.time.LocalDate

@Service
class StatusLogServiceImpl(
    private val statusLogRepository: AnimalStatusLogRepository,
    private val photoRepository: PhotoRepository,
    private val documentRepository: DocumentRepository,
    private val s3Service: S3Service,
    private val animalValidationService: AnimalValidationService
) : StatusLogService {

    @Transactional
    override fun addStatusLog(
        username: String,
        animalId: Long,
        request: StatusLogCreateRequest
    ): StatusLogResponse {
        val (user, animal) = animalValidationService.validateUserAndAnimal(username, animalId)

        val statusLog = statusLogRepository.save(AnimalStatusLog().apply {
            notes = request.notes
            logDate = request.logDate ?: LocalDate.now()
            this.animal = animal
            this.user = user
        })

        return statusLog.toDto(s3Service)
    }

    @Transactional
    override fun updateStatusLog(
        username: String,
        animalId: Long,
        statusLogId: Long,
        request: StatusLogUpdateRequest
    ): StatusLogResponse {
        val (_, _, statusLog) = animalValidationService.validateUserAndStatusLog(username, animalId, statusLogId)

        request.notes?.let { statusLog.notes = it }
        request.logDate?.let { statusLog.logDate = it }

        return statusLogRepository.save(statusLog).toDto(s3Service)
    }

    @Transactional
    override fun deleteStatusLog(username: String, animalId: Long, statusLogId: Long) {
        val (_, _, statusLog) = animalValidationService.validateUserAndStatusLog(username, animalId, statusLogId)

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

    @Transactional(readOnly = true)
    override fun getStatusLogs(username: String, animalId: Long): List<StatusLogResponse> {
        val (_, animal) = animalValidationService.validateUserAndAnimal(username, animalId)
        return statusLogRepository.findByAnimalId(animalId).map { it.toDto(s3Service) }
    }

    @Transactional(readOnly = true)
    override fun getStatusLog(id: Long): StatusLogResponse {
        val log = statusLogRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Status log not found") }
        return log.toDto(s3Service)
    }

}