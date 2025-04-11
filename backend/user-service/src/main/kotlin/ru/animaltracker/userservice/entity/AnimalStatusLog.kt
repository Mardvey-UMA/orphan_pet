package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "animal_status_log")
data class AnimalStatusLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "log_date")
    var logDate: LocalDate? = null,

    @Column(columnDefinition = "text")
    var notes: String? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id")
    var animal: Animal,

    @OneToMany(mappedBy = "animalStatusLog", cascade = [CascadeType.ALL], orphanRemoval = true)
    val statusLogPhotos: MutableSet<StatusLogPhoto> = mutableSetOf(),

    @OneToMany(mappedBy = "animalStatusLog", cascade = [CascadeType.ALL], orphanRemoval = true)
    val statusLogDocuments: MutableSet<StatusLogDocument> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val user: User
){
    constructor(notes: String?, logDate: LocalDate?, animal: Animal, user: User) : this(
        logDate = logDate,
        notes = notes,
        animal = animal,
        user = user,
        updatedAt = LocalDate.now()
    )
    fun addPhoto(photo: Photo): StatusLogPhoto {
        val statusLogPhoto = StatusLogPhoto(this, photo)
        statusLogPhotos.add(statusLogPhoto)
        return statusLogPhoto
    }

    fun addDocument(document: Document): StatusLogDocument {
        val statusLogDocument = StatusLogDocument(this, document)
        statusLogDocuments.add(statusLogDocument)
        return statusLogDocument
    }
}