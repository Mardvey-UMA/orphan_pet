package ru.animaltracker.userservice.service.interfaces

import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.S3FileResponse
import ru.animaltracker.userservice.dto.UserResponse
import ru.animaltracker.userservice.dto.UserUpdateRequest

interface UserService {
    fun updateUser(username: String, request: UserUpdateRequest): UserResponse
    fun getUser(username: String): UserResponse
     fun uploadUserPhoto(username: String, file: MultipartFile): S3FileResponse
}