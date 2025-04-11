package ru.animaltracker.userservice.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "animal")
data class Animal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "body_mass")
    val mass: BigDecimal? = null,

    @Column(name = "birth_date")
    val birthDate: LocalDate? = null,

    @Column(name = "created_at")
    val createdAt: LocalDate? = null,

    @Column(length = 255)
    val name: String? = null,

    @Column(columnDefinition = "text")
    val description: String? = null,

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
    val attributeValueHistories: MutableSet<AttributeValueHistory> = mutableSetOf()
)