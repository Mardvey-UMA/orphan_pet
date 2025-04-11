package ru.animaltracker.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.userservice.entity.User
import ru.animaltracker.userservice.entity.UserPhoto

interface UserPhotoRepository : JpaRepository<UserPhoto, Long> {
    fun deleteAllByUser(user: User)
}