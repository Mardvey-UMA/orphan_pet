package ru.animaltracker.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.userservice.entity.Animal

interface AnimalRepository : JpaRepository<Animal, Long>
