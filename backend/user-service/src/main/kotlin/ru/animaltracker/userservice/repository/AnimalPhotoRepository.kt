package ru.animaltracker.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.userservice.entity.AnimalPhoto

interface AnimalPhotoRepository : JpaRepository<AnimalPhoto, Long>{
    fun findByPhotoId(photoId: Long): AnimalPhoto?
}