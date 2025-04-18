package ru.animaltracker.userservice.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.dto.S3FileResponse
import ru.animaltracker.userservice.dto.UserResponse
import ru.animaltracker.userservice.dto.UserUpdateRequest
import ru.animaltracker.userservice.entity.Photo
import ru.animaltracker.userservice.entity.UserPhoto
import ru.animaltracker.userservice.repository.PhotoRepository
import ru.animaltracker.userservice.repository.UserPhotoRepository
import ru.animaltracker.userservice.repository.UserRepository
import ru.animaltracker.userservice.service.interfaces.S3Service
import ru.animaltracker.userservice.service.interfaces.UserService

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val s3Service: S3Service,
    private val photoRepository: PhotoRepository,
    private val userPhotoRepository: UserPhotoRepository
) : UserService {

    @Transactional
    override fun updateUser(username: String, request: UserUpdateRequest): UserResponse {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        request.firstName?.let { user.firstName = it }
        request.lastName?.let { user.lastName = it }
        request.city?.let { user.city = it }
        request.aboutMe?.let { user.aboutMe = it }
        val savedUser = userRepository.save(user)
        return savedUser.toDto(s3Service)
    }

    @Transactional(readOnly = true)
    override fun getUser(username: String): UserResponse {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        return user.toDto(s3Service)
    }

    @Transactional
    override fun uploadUserPhoto(username: String, file: MultipartFile): S3FileResponse {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        // Удаляем старое фото если есть
        user.userPhotos.firstOrNull()?.let { userPhoto ->
            userPhoto.photo?.objectKey?.let { s3Service.deleteFile(it) }
            photoRepository.delete(userPhoto.photo ?: throw IllegalStateException("Photo not found"))
            userPhotoRepository.delete(userPhoto)
        }

        // Загружаем новое фото
        val objectKey = s3Service.uploadFile(file, "users/$username/photos")
        val photo = photoRepository.save(Photo().apply {
            this.objectKey = objectKey
        })

        // Создаем связь пользователя с фото
        userPhotoRepository.save(UserPhoto().apply {
            this.user = user
            this.photo = photo
        })

        return S3FileResponse(
            objectKey = objectKey,
            presignedUrl = s3Service.generatePresignedUrl(objectKey)
        )
    }
}