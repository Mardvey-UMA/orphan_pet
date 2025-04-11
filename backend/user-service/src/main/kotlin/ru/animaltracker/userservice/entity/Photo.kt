package ru.animaltracker.userservice.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "photo")
data class Photo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "object_key", length = 512, unique = true)
    val objectKey: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDate? = null,

    @OneToMany(mappedBy = "photo", cascade = [CascadeType.ALL], orphanRemoval = true)
    val userPhotos: MutableSet<UserPhoto> = mutableSetOf(),

    @OneToMany(mappedBy = "photo", cascade = [CascadeType.ALL], orphanRemoval = true)
    val animalPhotos: MutableSet<AnimalPhoto> = mutableSetOf(),

    @OneToMany(mappedBy = "photo", cascade = [CascadeType.ALL], orphanRemoval = true)
    val statusLogPhotos: MutableSet<StatusLogPhoto> = mutableSetOf()
)