package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import ru.animaltracker.userservice.dto.ParameterChangeResponse
import ru.animaltracker.userservice.dto.StatusLogResponse
import ru.animaltracker.userservice.service.interfaces.S3Service
import java.math.BigDecimal
import java.time.LocalDate
@Entity
@Table(name = "animal_status_log")
class AnimalStatusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "log_date")
    var logDate: LocalDate? = null

    @Column(columnDefinition = "text")
    var notes: String? = null

    @Column(name = "updated_at")
    var updatedAt: LocalDate? = null

    @Column(name = "mass_change")
    var massChange: BigDecimal? = null

    @Column(name = "height_change")
    var heightChange: BigDecimal? = null

    @Column(name = "temperature_change")
    var temperatureChange: BigDecimal? = null

    @Column(name = "activity_level_change")
    var activityLevelChange: Int? = null

    @Column(name = "appetite_level_change")
    var appetiteLevelChange: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id")
    lateinit var animal: Animal

    @OneToMany(mappedBy = "animalStatusLog", cascade = [CascadeType.ALL], orphanRemoval = true)
    var statusLogPhotos: MutableSet<StatusLogPhoto> = mutableSetOf()

    @OneToMany(mappedBy = "animalStatusLog", cascade = [CascadeType.ALL], orphanRemoval = true)
    var statusLogDocuments: MutableSet<StatusLogDocument> = mutableSetOf()

    @OneToMany(mappedBy = "statusLog", cascade = [CascadeType.ALL], orphanRemoval = true)
    var parameterHistories: MutableSet<AnimalParameterHistory> = mutableSetOf()

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    lateinit var user: User

    fun toDto(s3Service: S3Service): StatusLogResponse {
        return StatusLogResponse(
            id = id,
            logDate = logDate ?: LocalDate.now(),
            notes = notes,
            massChange = massChange,
            heightChange = heightChange,
            temperatureChange = temperatureChange,
            activityLevelChange = activityLevelChange,
            appetiteLevelChange = appetiteLevelChange,
            photos = statusLogPhotos.mapNotNull { it.photo?.objectKey?.let { key -> s3Service.generatePresignedUrl(key) } },
            documents = statusLogDocuments.mapNotNull { it.document?.objectKey?.let { key -> s3Service.generatePresignedUrl(key) } },
            parameterChanges = parameterHistories.map { history ->
                val changes = mutableListOf<ParameterChangeResponse>()

                if (history.oldMass != null || history.newMass != null) {
                    changes.add(ParameterChangeResponse(
                        parameterName = "mass",
                        oldValue = history.oldMass?.toString() ?: "",
                        newValue = history.newMass?.toString() ?: "",
                        changedAt = history.recordedAt ?: LocalDate.now(),
                        changedBy = history.user.username ?: ""
                    ))
                }

                if (history.oldHeight != null || history.newHeight != null) {
                    changes.add(ParameterChangeResponse(
                        parameterName = "height",
                        oldValue = history.oldHeight?.toString() ?: "",
                        newValue = history.newHeight?.toString() ?: "",
                        changedAt = history.recordedAt ?: LocalDate.now(),
                        changedBy = history.user.username ?: ""
                    ))
                }

                if (history.oldTemperature != null || history.newTemperature != null) {
                    changes.add(ParameterChangeResponse(
                        parameterName = "temperature",
                        oldValue = history.oldTemperature?.toString() ?: "",
                        newValue = history.newTemperature?.toString() ?: "",
                        changedAt = history.recordedAt ?: LocalDate.now(),
                        changedBy = history.user.username ?: ""
                    ))
                }

                if (history.oldActivityLevel != null || history.newActivityLevel != null) {
                    changes.add(ParameterChangeResponse(
                        parameterName = "activityLevel",
                        oldValue = history.oldActivityLevel?.toString() ?: "",
                        newValue = history.newActivityLevel?.toString() ?: "",
                        changedAt = history.recordedAt ?: LocalDate.now(),
                        changedBy = history.user.username ?: ""
                    ))
                }

                if (history.oldAppetiteLevel != null || history.newAppetiteLevel != null) {
                    changes.add(ParameterChangeResponse(
                        parameterName = "appetiteLevel",
                        oldValue = history.oldAppetiteLevel?.toString() ?: "",
                        newValue = history.newAppetiteLevel?.toString() ?: "",
                        changedAt = history.recordedAt ?: LocalDate.now(),
                        changedBy = history.user.username ?: ""
                    ))
                }

                changes
            }.flatten()
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnimalStatusLog) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "AnimalStatusLog(id=$id)"
}