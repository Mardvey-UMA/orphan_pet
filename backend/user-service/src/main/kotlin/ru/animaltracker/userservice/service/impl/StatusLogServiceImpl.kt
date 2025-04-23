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
    private val parameterHistoryRepository: AnimalParameterHistoryRepository,
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
            massChange = request.massChange
            heightChange = request.heightChange
            temperatureChange = request.temperatureChange
            activityLevelChange = request.activityLevelChange
            appetiteLevelChange = request.appetiteLevelChange
            this.animal = animal
            this.user = user
        })

        saveParameterChanges(animal, user, statusLog)

        return statusLog.toDto(s3Service)
    }
    private fun saveParameterChanges(
        animal: Animal,
        user: User,
        statusLog: AnimalStatusLog
    ) {
        val history = AnimalParameterHistory().apply {
            recordedAt = statusLog.logDate ?: LocalDate.now()
            this.user = user
            this.animal = animal
            this.statusLog = statusLog
        }

        statusLog.massChange?.let {
            history.oldMass = animal.mass
            animal.mass = animal.mass?.add(it) ?: it
            history.newMass = animal.mass
        }

        statusLog.heightChange?.let {
            history.oldHeight = animal.height
            animal.height = animal.height?.add(it) ?: it
            history.newHeight = animal.height
        }

        statusLog.temperatureChange?.let {
            history.oldTemperature = animal.temperature
            animal.temperature = animal.temperature?.add(it) ?: it
            history.newTemperature = animal.temperature
        }

        statusLog.activityLevelChange?.let {
            history.oldActivityLevel = animal.activityLevel
            animal.activityLevel = it
            history.newActivityLevel = it
        }

        statusLog.appetiteLevelChange?.let {
            history.oldAppetiteLevel = animal.appetiteLevel
            animal.appetiteLevel = it
            history.newAppetiteLevel = it
        }

        if (history.hasChanges()) {
            parameterHistoryRepository.save(history)
        }
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

    @Transactional(readOnly = true)
    override fun getParameterHistory(
        username: String,
        animalId: Long,
        parameterName: String
    ): List<ParameterChangeResponse> {
        animalValidationService.validateUserAndAnimal(username, animalId)

        return parameterHistoryRepository.findByAnimalIdOrderByRecordedAtDesc(animalId)
            .filterNotNull()
            .mapNotNull { history ->
                when (parameterName.lowercase()) {
                    "mass" -> history.takeIf { it.oldMass != null || it.newMass != null }?.let {
                        ParameterChangeResponse(
                            parameterName = "mass",
                            oldValue = it.oldMass?.toString() ?: "",
                            newValue = it.newMass?.toString() ?: "",
                            changedAt = it.recordedAt ?: LocalDate.now(),
                            changedBy = it.user.username ?: ""
                        )
                    }
                    "height" -> history.takeIf { it.oldHeight != null || it.newHeight != null }?.let {
                        ParameterChangeResponse(
                            parameterName = "height",
                            oldValue = it.oldHeight?.toString() ?: "",
                            newValue = it.newHeight?.toString() ?: "",
                            changedAt = it.recordedAt ?: LocalDate.now(),
                            changedBy = it.user.username ?: ""
                        )
                    }
                    "temperature" -> history.takeIf { it.oldTemperature != null || it.newTemperature != null }?.let {
                        ParameterChangeResponse(
                            parameterName = "temperature",
                            oldValue = it.oldTemperature?.toString() ?: "",
                            newValue = it.newTemperature?.toString() ?: "",
                            changedAt = it.recordedAt ?: LocalDate.now(),
                            changedBy = it.user.username ?: ""
                        )
                    }
                    "activitylevel" -> history.takeIf { it.oldActivityLevel != null || it.newActivityLevel != null }?.let {
                        ParameterChangeResponse(
                            parameterName = "activityLevel",
                            oldValue = it.oldActivityLevel?.toString() ?: "",
                            newValue = it.newActivityLevel?.toString() ?: "",
                            changedAt = it.recordedAt ?: LocalDate.now(),
                            changedBy = it.user.username ?: ""
                        )
                    }
                    "appetitelevel" -> history.takeIf { it.oldAppetiteLevel != null || it.newAppetiteLevel != null }?.let {
                        ParameterChangeResponse(
                            parameterName = "appetiteLevel",
                            oldValue = it.oldAppetiteLevel?.toString() ?: "",
                            newValue = it.newAppetiteLevel?.toString() ?: "",
                            changedAt = it.recordedAt ?: LocalDate.now(),
                            changedBy = it.user.username ?: ""
                        )
                    }
                    else -> null
                }
            }
    }
}