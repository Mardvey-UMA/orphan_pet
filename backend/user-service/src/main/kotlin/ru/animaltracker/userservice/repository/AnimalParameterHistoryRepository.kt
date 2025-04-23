package ru.animaltracker.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.userservice.entity.AnimalParameterHistory

interface AnimalParameterHistoryRepository: JpaRepository<AnimalParameterHistory, Long> {
    fun findByAnimalIdOrderByRecordedAtDesc(animalId: Long): List<AnimalParameterHistory>
    fun findByStatusLogId(statusLogId: Long): List<AnimalParameterHistory>
}