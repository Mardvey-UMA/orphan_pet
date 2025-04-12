package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "value")
class Value{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(length = 1024)
    var value: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    var attribute: Attribute? = null

override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Value) return false
    return id == other.id
}

override fun hashCode(): Int = id.hashCode()

override fun toString(): String = "Value(id=$id, value=$value)"
}