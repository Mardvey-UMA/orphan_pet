package ru.animaltracker.userservice.entity

import jakarta.persistence.*
import ru.animaltracker.userservice.dto.AnimalResponse
import ru.animaltracker.userservice.service.interfaces.S3Service
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "animal")
class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "body_mass")
    var mass: BigDecimal? = null

    @Column(name = "birth_date")
    var birthDate: LocalDate? = null

    @Column(name = "created_at")
    var createdAt: LocalDate? = null

    @Column(length = 255)
    var name: String? = null

    @Column(columnDefinition = "text")
    var description: String? = null

    @OneToMany(mappedBy = "animal",  orphanRemoval = true)
    var attributes: MutableSet<Attribute> = mutableSetOf()

    @OneToMany(mappedBy = "animal",  orphanRemoval = true)
    var animalPhotos: MutableSet<AnimalPhoto> = mutableSetOf()

    @OneToMany(mappedBy = "animal",  orphanRemoval = true)
    var animalUsers: MutableSet<AnimalUser> = mutableSetOf()

    @OneToMany(mappedBy = "animal",  orphanRemoval = true)
    var documents: MutableSet<Document> = mutableSetOf()

    @OneToMany(mappedBy = "animal",  orphanRemoval = true)
    var statusLogs: MutableSet<AnimalStatusLog> = mutableSetOf()

    @OneToMany(mappedBy = "animal",  orphanRemoval = true)
    var attributeValueHistories: MutableSet<AttributeValueHistory> = mutableSetOf()

    fun addStatusLog(statusLog: AnimalStatusLog) {
        statusLogs.add(statusLog)
        statusLog.animal = this
    }

    fun addPhoto(photo: Photo): AnimalPhoto {
        val animalPhoto = AnimalPhoto().apply {
            this.animal = this@Animal
            this.photo = photo
        }
        animalPhotos.add(animalPhoto)
        photo.animalPhotos.add(animalPhoto)
        return animalPhoto
    }

    fun addDocument(document: Document) {
        documents.add(document)
        document.animal = this
    }

    fun toDto(s3Service: S3Service): AnimalResponse {
        return AnimalResponse(
            id = id,
            name = name,
            description = description,
            birthDate = birthDate,
            mass = mass,
            attributes = attributes.map { it.toDto() },
            photos = animalPhotos.mapNotNull { it.photo?.objectKey?.let { it1 -> s3Service.generatePresignedUrl(it1) } },
            documents = documents.mapNotNull { it.objectKey?.let { it1 -> s3Service.generatePresignedUrl(it1) } },
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Animal) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Animal(id=$id, name=$name)"
}