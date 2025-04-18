package ru.animaltracker.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.authservice.entity.Role


interface RoleRepository: JpaRepository<Role, Long> {
    fun findByName(name: String): Role?
}