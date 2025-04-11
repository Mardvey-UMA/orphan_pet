package ru.animaltracker.userservice.entity

import jakarta.persistence.*
import ru.animaltracker.userservice.dto.UserResponse
import java.time.LocalDate

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 255)
    val email: String? = null,

    @Column(length = 255)
    val username: String? = null,

    @Column(name = "first_name", length = 255)
    var firstName: String? = null,

    @Column(name = "last_name", length = 255)
    var lastName: String? = null,

    @Column(length = 255)
    var city: String? = null,

    @Column(name = "about_me", length = 255)
    var aboutMe: String? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val userPhotos: MutableSet<UserPhoto> = mutableSetOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val animalUsers: MutableSet<AnimalUser> = mutableSetOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val attributeValueHistories: MutableSet<AttributeValueHistory> = mutableSetOf()
) {
    fun addAnimal(animal: Animal) {
        val animalUser = AnimalUser().apply {
            this.user = this@User
            this.animal = animal
        }
        animalUsers.add(animalUser)
        animal.animalUsers.add(animalUser)
    }

    fun getAnimals(): List<Animal> = animalUsers.mapNotNull { it.animal }

    fun toDto(): UserResponse {
        return UserResponse(
            username = username ?: "",
            firstName = firstName,
            lastName = lastName,
            city = city,
            aboutMe = aboutMe,
            photoUrl = userPhotos.firstOrNull()?.photo?.objectKey
        )
    }
}