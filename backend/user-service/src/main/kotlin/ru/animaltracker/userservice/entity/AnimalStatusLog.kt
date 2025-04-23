package ru.animaltracker.userservice.entity
import jakarta.persistence.*
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
            photos = statusLogPhotos.mapNotNull { it.photo?.objectKey?.let { it1 -> s3Service.generatePresignedUrl(it1) } },
            documents = statusLogDocuments.mapNotNull { it.document?.objectKey?.let { it1 ->
                s3Service.generatePresignedUrl(
                    it1
                )
            } }
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