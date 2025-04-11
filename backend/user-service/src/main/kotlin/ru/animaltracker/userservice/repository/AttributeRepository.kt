package ru.animaltracker.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.userservice.entity.Attribute

interface AttributeRepository : JpaRepository<Attribute, Short> {
    fun findByAnimalId(animalId: Long): List<Attribute>
}