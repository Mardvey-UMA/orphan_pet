package ru.animaltracker.userservice.service.interfaces

import ru.animaltracker.userservice.dto.*

interface AnimalManagementService {
    fun createAnimal(username: String, request: AnimalCreateRequest): AnimalResponse
    fun getAnimal(username: String, animalId: Long): AnimalResponse
    fun getUserAnimals(username: String): List<AnimalResponse>
    fun deleteAnimal(username: String, animalId: Long)
    fun updateAnimal(username: String, animalId: Long, request: AnimalUpdateRequest): AnimalResponse
    fun addAttribute(username: String, animalId: Long, request: AttributeRequest): AttributeResponse
    fun updateAttribute(username: String, animalId: Long, attributeId: Short, request: AttributeUpdateRequest): AttributeResponse
    fun deleteAttribute(username: String, animalId: Long, attributeId: Short)
}