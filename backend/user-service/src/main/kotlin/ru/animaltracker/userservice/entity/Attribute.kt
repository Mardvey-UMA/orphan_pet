package ru.animaltracker.userservice.entity
import jakarta.persistence.*
import ru.animaltracker.userservice.dto.AttributeResponse
import java.time.LocalDate

@Entity
@Table(name = "attribute")
class Attribute(
    @Column(length = 255)
    var name: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    var animal: Animal? = null,
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Short = 0

    @OneToMany(mappedBy = "attribute", cascade = [CascadeType.ALL], orphanRemoval = true)
    var values: MutableSet<Value> = mutableSetOf()
        protected set

    @OneToMany(mappedBy = "attribute")
    var valueHistories: MutableSet<AttributeValueHistory> = mutableSetOf()
        protected set

    fun addValue(valueStr: String) {
        val value = Value(valueStr).apply {
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
}