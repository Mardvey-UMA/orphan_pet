package ru.doedating.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "document")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 255)
    val type: String? = null,

    @Column(name = "object_key", length = 512)
    val objectKey: String? = null,

    @Column(name = "document_name", length = 255)
    val documentName: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    val animal: Animal? = null,

    @OneToMany(mappedBy = "document", cascade = [CascadeType.ALL], orphanRemoval = true)
    val statusLogDocuments: MutableSet<StatusLogDocument> = mutableSetOf()
)
