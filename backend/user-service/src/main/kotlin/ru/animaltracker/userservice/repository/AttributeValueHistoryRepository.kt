package ru.animaltracker.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.userservice.entity.AttributeValueHistory

interface AttributeValueHistoryRepository: JpaRepository<AttributeValueHistory, Long> {
}