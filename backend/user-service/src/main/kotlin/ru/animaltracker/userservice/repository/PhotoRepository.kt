package ru.animaltracker.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.userservice.entity.Photo

interface PhotoRepository : JpaRepository<Photo, Long>