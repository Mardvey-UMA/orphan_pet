package ru.doedating.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.doedating.userservice.entity.User

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun findByUsername(username: String): User?
}