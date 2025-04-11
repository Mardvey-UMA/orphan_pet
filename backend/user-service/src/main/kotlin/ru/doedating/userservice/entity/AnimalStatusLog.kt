package ru.doedating.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "animal_status_log")
data class AnimalStatusLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "log_date")
    val logDate: LocalDate? = null,

    @Column(columnDefinition = "text")
    val notes: String? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id")
    val animal: Animal,

    @OneToMany(mappedBy = "animalStatusLog", cascade = [CascadeType.ALL], orphanRemoval = true)
    val statusLogPhotos: MutableSet<StatusLogPhoto> = mutableSetOf(),

    @OneToMany(mappedBy = "animalStatusLog", cascade = [CascadeType.ALL], orphanRemoval = true)
    val statusLogDocuments: MutableSet<StatusLogDocument> = mutableSetOf()
)