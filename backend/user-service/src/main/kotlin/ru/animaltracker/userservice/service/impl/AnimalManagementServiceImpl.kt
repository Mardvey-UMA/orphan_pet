package ru.animaltracker.userservice.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.transaction.annotation.Transactional
import ru.animaltracker.userservice.dto.*
import ru.animaltracker.userservice.entity.*
import ru.animaltracker.userservice.repository.*
import ru.animaltracker.userservice.service.interfaces.AnimalManagementService
import ru.animaltracker.userservice.service.interfaces.S3Service
import java.nio.file.AccessDeniedException
import java.time.LocalDate

class AnimalManagementServiceImpl(
    private val userRepository: UserRepository,
    private val animalRepository: AnimalRepository,
    private val attributeRepository: AttributeRepository,
    private val documentRepository: DocumentRepository,
    private val photoRepository: PhotoRepository,
    private val attributeValueHistoryRepository: AttributeValueHistoryRepository,
    private val s3Service: S3Service,
) : AnimalManagementService {
    @Transactional
    override fun createAnimal(username: String, request: AnimalCreateRequest): AnimalResponse {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        val animal = Animal().apply {
            name = request.name
            description = request.description
            birthDate = request.birthDate
            mass = request.mass
        }

        val savedAnimal = animalRepository.save(animal)

        request.attributes.forEach { attrRequest ->
            val attribute = Attribute().apply {
                name = attrRequest.name
                this.animal = savedAnimal
                addValue(attrRequest.value)
            }
            attributeRepository.save(attribute)
        }

        user.addAnimal(savedAnimal)
        userRepository.save(user)

        return savedAnimal.toDto(s3Service)
    }

    @Transactional(readOnly = true)
    override fun getAnimalAttributesHistory(animalId: Long): List<AttributeHistoryResponse> {
        val animal = animalRepository.findById(animalId)
            .orElseThrow { EntityNotFoundException("Animal not found") }

        return attributeRepository.findByAnimalId(animalId)
            .flatMap { attribute ->
                attribute.valueHistories.map { history ->
                    AttributeHistoryResponse(
                        attributeName = attribute.name ?: "",
                        oldValue = history.value,
                        changedAt = history.recordedAt ?: LocalDate.now(),
                        changedBy = history.user.username ?: ""
                    )
                }
            }
    }

    @Transactional(readOnly = true)
    override fun getUserAnimals(username: String): List<AnimalResponse> {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        return user.animalUsers.mapNotNull { it.animal?.toDto(s3Service) }
    }

    @Transactional
    override fun deleteAnimal(username: String, animalId: Long) {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        animal.documents.forEach {
            it.objectKey?.let { key -> s3Service.deleteFile(key) }
            documentRepository.delete(it)
        }

        animal.animalPhotos.forEach {
            it.photo?.objectKey?.let { key -> s3Service.deleteFile(key) }
            it.photo?.let { photo -> photoRepository.delete(photo) }
        }

        animalRepository.delete(animal)
    }

    @Transactional
    override fun updateAnimal(username: String, animalId: Long, request: AnimalUpdateRequest): AnimalResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        request.name?.let { animal.name = it }
        request.description?.let { animal.description = it }
        request.birthDate?.let { animal.birthDate = it }
        request.mass?.let { animal.mass = it }

        return animalRepository.save(animal).toDto(s3Service)
    }

    @Transactional
    override fun addAttribute(
        username: String,
        animalId: Long,
        request: AttributeRequest
    ): AttributeResponse {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        val attribute = Attribute().apply {
            name = request.name
            this.animal = animal
        }.also { attr ->
            attr.values.add(Value().apply {
                value = request.value
                attribute = attr
            })
        }

        return attributeRepository.save(attribute).toDto()
    }

    @Transactional
    override fun updateAttribute(
        username: String,
        animalId: Long,
        attributeId: Short,
        request: AttributeUpdateRequest
    ): AttributeResponse {
        val (user, animal) = validateUserAndAnimal(username, animalId)
        val attribute = attributeRepository.findById(attributeId)
            .orElseThrow { EntityNotFoundException("Attribute not found") }

        if (attribute.animal?.id != animalId) {
            throw AccessDeniedException("Attribute doesn't belong to this animal")
        }

        request.name?.let { attribute.name = it }

        if (request.value != null) {
            val value = attribute.values.firstOrNull() ?: Value().apply {
                this.attribute = attribute
            }
            value.value = request.value
            attribute.values.clear()
            attribute.values.add(value)

            attributeValueHistoryRepository.save(AttributeValueHistory().apply {
                this.value = request.value
                this.recordedAt = LocalDate.now()
                this.user = user
                this.animal = animal
                this.attribute = attribute
            })
        }

        return attributeRepository.save(attribute).toDto()
    }

    @Transactional
    override fun deleteAttribute(username: String, animalId: Long, attributeId: Short) {
        val (_, _) = validateUserAndAnimal(username, animalId)
        val attribute = attributeRepository.findById(attributeId)
            .orElseThrow { EntityNotFoundException("Attribute not found") }

        if (attribute.animal?.id != animalId) {
            throw AccessDeniedException("Attribute doesn't belong to this animal")
        }

        attributeRepository.delete(attribute)
    }

    @Transactional(readOnly = true)
    private fun validateUserAndAnimal(username: String, animalId: Long): Pair<User, Animal> {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        val animal = animalRepository.findById(animalId)
            .orElseThrow { EntityNotFoundException("Animal not found") }

        if (animal.animalUsers.none { it.user?.id == user.id }) {
            throw AccessDeniedException("User doesn't have access to this animal")
        }

        return user to animal
    }
}