package ru.doedating.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "user_photo")
data class UserPhoto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    val photo: Photo? = null
)