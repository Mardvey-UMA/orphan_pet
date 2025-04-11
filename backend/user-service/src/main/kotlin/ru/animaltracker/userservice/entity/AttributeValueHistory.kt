package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "attribute_value_history")
data class AttributeValueHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 1024)
    val value: String? = null,

    @Column(name = "recorded_at")
    val recordedAt: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id")
    val animal: Animal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    val attribute: Attribute? = null
)
