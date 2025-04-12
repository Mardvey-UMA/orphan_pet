package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "attribute_value_history")
class AttributeValueHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(length = 1024)
    var value: String? = null

    @Column(name = "recorded_at")
    var recordedAt: LocalDate? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    lateinit var user: User

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id")
    lateinit var animal: Animal

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    var attribute: Attribute? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttributeValueHistory) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "AttributeValueHistory(id=$id)"
}