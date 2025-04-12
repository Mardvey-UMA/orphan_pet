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
        return savedUser.toDto()
    }

    @Transactional(readOnly = true)
    override fun getUser(username: String): UserResponse {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        return user.toDto()
    }

    @Transactional
    override  fun uploadUserPhoto(username: String, file: MultipartFile): S3FileResponse {
        val user = 
            userRepository.findByUsername(username)
        
            ?: throw EntityNotFoundException("User not found")

        user.userPhotos.firstOrNull()?.let {
            photoRepository.deleteById(it.photo?.id!!)
            s3Service.deleteFile(it.photo.objectKey!!)
        }

        val objectKey = s3Service.uploadFile(file, "users/$username/photos")
        val photo = 
            photoRepository.save(Photo(objectKey = objectKey))
        

        
            userPhotoRepository.deleteAllByUser(user)
        
        
            userPhotoRepository.save(UserPhoto(user = user, photo = photo))
        

        return S3FileResponse(
            objectKey = objectKey,
            presignedUrl = s3Service.generatePresignedUrl(objectKey)
        )
    }
}