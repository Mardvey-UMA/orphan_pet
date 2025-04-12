package ru.animaltracker.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.userservice.entity.AnimalStatusLog

interface AnimalStatusLogRepository : JpaRepository<AnimalStatusLog, Long>{
    fun findByAnimalId(animalId: Long): List<AnimalStatusLog>
}