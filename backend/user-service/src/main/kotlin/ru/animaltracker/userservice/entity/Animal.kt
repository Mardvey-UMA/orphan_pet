package ru.animaltracker.userservice.entity

import jakarta.persistence.*
import ru.animaltracker.userservice.dto.AnimalResponse
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "animal")
data class Animal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "body_mass")
    var mass: BigDecimal? = null,

    @Column(name = "birth_date")
    var birthDate: LocalDate? = null,

    @Column(name = "created_at")
    val createdAt: LocalDate? = null,

    @Column(length = 255)
    var name: String? = null,

    @Column(columnDefinition = "text")
    var description: String? = null,

    @OneToMany(mappedBy = "animal", cascade = [CascadeType.ALL], orphanRemoval = true)
    val attributes: MutableSet<Attribute> = mutableSetOf(),

    @OneToMany(mappedBy = "animal", cascade = [CascadeType.ALL], orphanRemoval = true)
    val animalPhotos: MutableSet<AnimalPhoto> = mutableSetOf(),

    @OneToMany(mappedBy = "animal", cascade = [CascadeType.ALL], orphanRemoval = true)
    val animalUsers: MutableSet<AnimalUser> = mutableSetOf(),

    @OneToMany(mappedBy = "animal", cascade = [CascadeType.ALL], orphanRemoval = true)
    val documents: MutableSet<Document> = mutableSetOf(),

    @OneToMany(mappedBy = "animal", cascade = [CascadeType.ALL], orphanRemoval = true)
    val statusLogs: MutableSet<AnimalStatusLog> = mutableSetOf(),

    @OneToMany(mappedBy = "animal", cascade = [CascadeType.ALL], orphanRemoval = true)
    val attributeValueHistories: MutableSet<AttributeValueHistory> = mutableSetOf(),
){
    fun addStatusLog(statusLog: AnimalStatusLog) {
        statusLogs.add(statusLog)
        statusLog.animal = this
    }

    fun addPhoto(photo: Photo): AnimalPhoto {
        val animalPhoto = AnimalPhoto(animal = this, photo = photo)
        animalPhotos.add(animalPhoto)
        photo.animalPhotos.add(animalPhoto)
        return animalPhoto
    }

    fun addDocument(document: Document) {
        documents.add(document)
        document.animal = this
    }
    fun toDto(): AnimalResponse {
        return AnimalResponse(
            id = id,
            name = name,
            description = description,
            birthDate = birthDate,
            mass = mass,
            attributes = attributes.map { it.toDto() },
            photos = animalPhotos.mapNotNull { it.photo?.objectKey }
        )
    }
}