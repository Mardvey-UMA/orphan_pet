package ru.animaltracker.userservice.entity

import jakarta.persistence.*
import ru.animaltracker.userservice.dto.UserResponse
import ru.animaltracker.userservice.service.interfaces.S3Service
import java.time.LocalDate

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(length = 255)
    var email: String? = null

    @Column(length = 255)
    var username: String? = null

    @Column(name = "first_name", length = 255)
    var firstName: String? = null

    @Column(name = "last_name", length = 255)
    var lastName: String? = null

    @Column(length = 255)
    var city: String? = null

    @Column(name = "about_me", length = 255)
    var aboutMe: String? = null

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var userPhotos: MutableSet<UserPhoto> = mutableSetOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var animalUsers: MutableSet<AnimalUser> = mutableSetOf()

    fun addAnimal(animal: Animal) {
        val animalUser = AnimalUser().apply {
            this.user = this@User
            this.animal = animal
        }
        animalUsers.add(animalUser)
        animal.animalUsers.add(animalUser)
    }

    fun getAnimals(): List<Animal> = animalUsers.mapNotNull { it.animal }

    fun toDto(s3Service : S3Service): UserResponse {
        return UserResponse(
            username = username ?: "",
            firstName = firstName,
            lastName = lastName,
            city = city,
            aboutMe = aboutMe,
            photoUrl = userPhotos.firstOrNull()?.photo?.objectKey?.let { s3Service.generatePresignedUrl(it) }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "User(id=$id, username=$username)"
}