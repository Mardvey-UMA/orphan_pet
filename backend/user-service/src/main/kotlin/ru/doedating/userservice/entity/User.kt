package ru.doedating.userservice.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "_user")
@EntityListeners(AuditingEntityListener::class)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = true)
    var vkId: Long? = null,

    @Column(nullable = true)
    var firstName: String? = null,

    @Column(nullable = true)
    var lastName: String? = null,

    @Column(nullable = true)
    var birthday: LocalDate? = null,

    @Column(nullable = true)
    var age: Int? = null,

    @Column(nullable = true)
    var gender: String? = null,

    @Column(nullable = true)
    var city: String? = null,

    @Column(nullable = true)
    var country: String? = null,

    @Column(nullable = true)
    var job: String? = null,

    @Column(nullable = true)
    var education: String? = null,

    @Column(nullable = true)
    var aboutMe: String? = null,

    @Column(nullable = true)
    var theme: String? = null,

    @Column(nullable = true)
    var chatId: Long? = null,

    @Column(nullable = true)
    var telegramId: Long? = null,

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val photos: MutableList<Photos> = mutableListOf(),

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_at")
    private var updatedAt: LocalDateTime? = null
)
