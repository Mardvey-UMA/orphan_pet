package ru.animaltracker.userservice.service.interfaces

import org.mapstruct.Mapper
import ru.animaltracker.userservice.dto.AnimalResponse
import ru.animaltracker.userservice.dto.AttributeResponse
import ru.animaltracker.userservice.dto.StatusLogResponse
import ru.animaltracker.userservice.entity.*

@Mapper(componentModel = "spring")
interface AnimalMapper {
    fun toResponse(animal: Animal): AnimalResponse
    fun toStatusLogResponse(statusLog: AnimalStatusLog): StatusLogResponse
    fun toAttributeResponse(attribute: Attribute): AttributeResponse
    fun mapAttributes(attributes: Set<Attribute>): List<AttributeResponse>
    fun mapPhotos(photos: Set<AnimalPhoto>): List<String>
    fun mapStatusLogPhotos(photos: Set<StatusLogPhoto>): List<String>
    fun mapStatusLogDocuments(documents: Set<StatusLogDocument>): List<String>
}