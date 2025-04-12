package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "animal_photo")
class AnimalPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    var animal: Animal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    var photo: Photo? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnimalPhoto) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "AnimalPhoto(id=$id)"
}