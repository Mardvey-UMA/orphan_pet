package ru.doedating.userservice.entity
import jakarta.persistence.*
import java.time.LocalDate
@Entity
@Table(name = "attribute")
data class Attribute(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Short = 0,

    @Column(length = 255)
    val name: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    val animal: Animal? = null,

    @OneToMany(mappedBy = "attribute", cascade = [CascadeType.ALL], orphanRemoval = true)
    val values: MutableSet<Value> = mutableSetOf(),

    @OneToMany(mappedBy = "attribute", cascade = [CascadeType.ALL], orphanRemoval = true)
    val attributeValueHistories: MutableSet<AttributeValueHistory> = mutableSetOf()
)