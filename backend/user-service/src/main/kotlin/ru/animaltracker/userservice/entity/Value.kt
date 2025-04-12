package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "value")
class Value(
    @Column(length = 1024)
    var value: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    var attribute: Attribute? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}