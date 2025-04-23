package ru.animaltracker.userservice.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "animal_parameter_history")
class AnimalParameterHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "recorded_at")
    var recordedAt: LocalDate? = null

    @Column(name = "old_mass")
    var oldMass: BigDecimal? = null

    @Column(name = "new_mass")
    var newMass: BigDecimal? = null

    @Column(name = "old_height")
    var oldHeight: BigDecimal? = null

    @Column(name = "new_height")
    var newHeight: BigDecimal? = null

    @Column(name = "old_temperature")
    var oldTemperature: BigDecimal? = null

    @Column(name = "new_temperature")
    var newTemperature: BigDecimal? = null

    @Column(name = "old_activity_level")
    var oldActivityLevel: Int? = null

    @Column(name = "new_activity_level")
    var newActivityLevel: Int? = null

    @Column(name = "old_appetite_level")
    var oldAppetiteLevel: Int? = null

    @Column(name = "new_appetite_level")
    var newAppetiteLevel: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id")
    lateinit var animal: Animal

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    lateinit var user: User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_log_id")
    var statusLog: AnimalStatusLog? = null

    fun hasChanges(): Boolean {
        return oldMass != null || newMass != null ||
                oldHeight != null || newHeight != null ||
                oldTemperature != null || newTemperature != null ||
                oldActivityLevel != null || newActivityLevel != null ||
                oldAppetiteLevel != null || newAppetiteLevel != null
    }
}