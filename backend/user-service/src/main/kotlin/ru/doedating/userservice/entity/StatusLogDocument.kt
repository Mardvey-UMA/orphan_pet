package ru.doedating.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "status_log_document")
data class StatusLogDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    val document: Document? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_status_log_id")
    val animalStatusLog: AnimalStatusLog? = null
)