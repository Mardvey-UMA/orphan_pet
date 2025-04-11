package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "animal_user")
data class AnimalUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    val animal: Animal? = null
)
