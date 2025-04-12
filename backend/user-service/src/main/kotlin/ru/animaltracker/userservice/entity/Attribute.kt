package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import ru.animaltracker.userservice.dto.AttributeResponse
import java.time.LocalDate

@Entity
@Table(name = "attribute")
class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Short = 0

    @Column(length = 255)
    var name: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    var animal: Animal? = null

    @OneToMany(mappedBy = "attribute", cascade = [CascadeType.ALL], orphanRemoval = true)
    var values: MutableSet<Value> = mutableSetOf()

    @OneToMany(mappedBy = "attribute")
    var valueHistories: MutableSet<AttributeValueHistory> = mutableSetOf()

    fun addValue(valueStr: String) {
        val value = Value().apply {
            this.value = valueStr
            this.attribute = this@Attribute
        }
        values.add(value)
    }

    fun addHistory(history: AttributeValueHistory) {
        history.attribute = this
        valueHistories.add(history)
    }

    fun toDto(): AttributeResponse {
        return AttributeResponse(
            id = id,
            name = name,
            value = values.firstOrNull()?.value
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Attribute) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Attribute(id=$id)"
}