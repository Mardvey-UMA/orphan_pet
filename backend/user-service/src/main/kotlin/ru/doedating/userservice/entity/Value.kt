package ru.doedating.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "value")
data class Value(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 1024)
    val value: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    val attribute: Attribute? = null
)