package ru.doedating.userservice.service.interfaces

import org.springframework.web.multipart.MultipartFile
import ru.doedating.userservice.dto.FileResponseDto
import ru.doedating.userservice.dto.UserDto
import ru.doedating.userservice.dto.UserUpdateDto
import ru.doedating.userservice.entity.User

interface UserService {
    fun getUserInfo(username: String): UserDto
    fun updateUserInfo(username: String, updateDto: UserUpdateDto): UserDto
    fun uploadUserPhoto(username: String, file: MultipartFile): FileResponseDto
    fun getUserPhotos(username: String): List<FileResponseDto>
    fun deleteUserPhoto(username: String, photoId: Long)
}