package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "status_log_document")
class StatusLogDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    var document: Document? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_status_log_id")
    var animalStatusLog: AnimalStatusLog? = null

    constructor()

    constructor(statusLog: AnimalStatusLog, document: Document) : this() {
        this.animalStatusLog = statusLog
        this.document = document
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StatusLogDocument) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "StatusLogDocument(id=$id)"
}