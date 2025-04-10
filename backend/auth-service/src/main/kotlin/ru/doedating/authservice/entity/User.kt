package ru.doedating.authservice.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.doedating.authservice.enums.Provider
import java.security.Principal
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class) // Чтобы дата создания и обновления сразу вбивались при создании юзера
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY - По мере заполнения просто инкремент id
    var id: Long? = null,

    @Column(nullable = false, unique = true) var email: String,

    @Column(nullable = false, unique = true)
    private var username: String,

    @Column(nullable = true)
    private var password: String,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var roles: MutableSet<Role> = mutableSetOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private var tokens: MutableSet<Token> = mutableSetOf(),

    @Column(nullable = true) var vkId: Long? = null,

    @Column(nullable = false) var provider: Provider,

    @Column(nullable = false) var enabled: Boolean,

    @Column(nullable = false)
    private var accountLocked: Boolean,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false) var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_at")
    private var updatedAt: LocalDateTime? = null

) : UserDetails, Principal{
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = roles.map { SimpleGrantedAuthority(it.name) }.toMutableSet()

    override fun isEnabled(): Boolean = enabled

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = !accountLocked

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun getName(): String = username

    fun setPassword(newPassword: String) {
        this.password = newPassword
    }
}