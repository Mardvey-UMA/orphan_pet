package ru.animaltracker.userservice.service.interfaces

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import ru.animaltracker.userservice.dto.UserResponse
import ru.animaltracker.userservice.entity.User
import ru.animaltracker.userservice.entity.UserPhoto

@Mapper(componentModel = "spring")
interface UserMapper {

    @Mapping(target = "photoUrl", source = "userPhotos")
    fun toResponse(user: User): UserResponse

    fun mapPhoto(userPhotos: Set<UserPhoto>): String? =
        userPhotos.firstOrNull()?.photo?.objectKey
}