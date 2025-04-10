package ru.doedating.userservice.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "photo")
@EntityListeners(AuditingEntityListener::class)
class Photo (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "photo_url", nullable = false, unique = false)
    var photoUrl: String,

    @Column(name = "object_key", nullable = false, unique = false)
    var objectKey: String,

    @Column(nullable = false, unique = false)
    var mimetype: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    val user: User,
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private var createdAt: LocalDateTime? = null

    )