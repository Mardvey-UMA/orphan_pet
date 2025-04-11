package ru.animaltracker.userservice.service.impl

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.doedating.userservice.dto.FileResponseDto
import ru.doedating.userservice.dto.UserDto
import ru.doedating.userservice.dto.UserUpdateDto
import ru.doedating.userservice.entity.Photo
import ru.doedating.userservice.entity.UserPhoto
import ru.doedating.userservice.repository.UserRepository
import ru.doedating.userservice.service.interfaces.UserService
import java.time.LocalDate
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository,
    private val userPhotoRepository: UserPhotoRepository,
    private val s3Service: S3Service,
    private val mapper: UserMapper
) : UserService {

    override fun getUserInfo(username: String): UserDto {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")
        return mapper.toDto(user)
    }

    @Transactional
    override fun updateUserInfo(username: String, updateDto: UserUpdateDto): UserDto {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        user.apply {
            firstName = updateDto.firstName ?: firstName
            lastName = updateDto.lastName ?: lastName
            city = updateDto.city ?: city
            aboutMe = updateDto.aboutMe ?: aboutMe
        }

        return mapper.toDto(userRepository.save(user))
    }

    @Transactional
    override fun uploadUserPhoto(username: String, file: MultipartFile): FileResponseDto {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        val key = "users/$username/photos/${UUID.randomUUID()}"
        val s3Key = s3Service.uploadFile(key, file)

        val photo = photoRepository.save(
            Photo(
                objectKey = s3Key,
                createdAt = LocalDate.now()
            )
        )

        userPhotoRepository.save(
            UserPhoto(
                user = user,
                photo = photo
            )
        )

        return FileResponseDto(
            id = photo.id,
            url = s3Service.getFileUrl(s3Key),
            description = null,
            createdAt = photo.createdAt
        )
    }

    override fun getUserPhotos(username: String): List<FileResponseDto> {
        return userPhotoRepository.findAllByUserUsername(username)
            .map { it.photo!! }
            .map { photo ->
                FileResponseDto(
                    id = photo.id,
                    url = s3Service.getFileUrl(photo.objectKey!!),
                    description = null,
                    createdAt = photo.createdAt
                )
            }
    }

    @Transactional
    override fun deleteUserPhoto(username: String, photoId: Long) {
        val userPhoto = userPhotoRepository.findByUserUsernameAndPhotoId(username, photoId)
            ?: throw EntityNotFoundException("Photo not found")

        s3Service.deleteFile(userPhoto.photo!!.objectKey!!)
        photoRepository.deleteById(photoId)
    }
}