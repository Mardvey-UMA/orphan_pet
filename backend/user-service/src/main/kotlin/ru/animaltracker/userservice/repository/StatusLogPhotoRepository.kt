package ru.animaltracker.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.userservice.entity.StatusLogPhoto

interface StatusLogPhotoRepository: JpaRepository<StatusLogPhoto, Long> {
}