package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "animal_photo")
data class AnimalPhoto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    val animal: Animal? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    val photo: Photo? = null
)