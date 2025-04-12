package ru.animaltracker.userservice.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "photo")
class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "object_key", length = 512, unique = true)
    var objectKey: String? = null

    @Column(name = "created_at")
    var createdAt: LocalDate? = null

    @OneToMany(mappedBy = "photo", cascade = [CascadeType.ALL], orphanRemoval = true)
    var userPhotos: MutableSet<UserPhoto> = mutableSetOf()

    @OneToMany(mappedBy = "photo", cascade = [CascadeType.ALL], orphanRemoval = true)
    var animalPhotos: MutableSet<AnimalPhoto> = mutableSetOf()

    @OneToMany(mappedBy = "photo", cascade = [CascadeType.ALL], orphanRemoval = true)
    var statusLogPhotos: MutableSet<StatusLogPhoto> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Photo) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Photo(id=$id)"
}