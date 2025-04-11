package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "status_log_photo")
data class StatusLogPhoto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    val photo: Photo? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_status_log_id")
    val animalStatusLog: AnimalStatusLog? = null
)