package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "status_log_photo")
class StatusLogPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    var photo: Photo? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_status_log_id")
    var animalStatusLog: AnimalStatusLog? = null

    constructor()

    constructor(statusLog: AnimalStatusLog, photo: Photo) : this() {
        this.animalStatusLog = statusLog
        this.photo = photo
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StatusLogPhoto) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "StatusLogPhoto(id=$id)"
}