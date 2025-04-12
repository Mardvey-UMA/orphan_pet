package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "document")
class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(length = 255)
    var type: String? = null

    @Column(name = "object_key", length = 512)
    var objectKey: String? = null

    @Column(name = "document_name", length = 255)
    var documentName: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    var animal: Animal? = null

    @OneToMany(mappedBy = "document", cascade = [CascadeType.ALL], orphanRemoval = true)
    var statusLogDocuments: MutableSet<StatusLogDocument> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Document) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Document(id=$id)"
}