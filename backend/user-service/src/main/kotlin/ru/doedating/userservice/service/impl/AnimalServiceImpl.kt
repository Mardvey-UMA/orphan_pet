package ru.doedating.userservice.service.impl

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.doedating.userservice.dto.AnimalDto
import ru.doedating.userservice.dto.AnimalStatusLogDto
import ru.doedating.userservice.dto.AttributeHistoryDto
import ru.doedating.userservice.dto.FileResponseDto
import ru.doedating.userservice.entity.Animal
import ru.doedating.userservice.entity.AnimalStatusLog
import ru.doedating.userservice.repository.UserRepository
import ru.doedating.userservice.service.S3Service
import ru.doedating.userservice.service.interfaces.AnimalService
@Service
class AnimalServiceImpl(
    private val animalRepository: AnimalRepository,
    private val userRepository: UserRepository,
    private val attributeRepository: AttributeRepository,
    private val valueRepository: ValueRepository,
    private val animalUserRepository: AnimalUserRepository,
    private val photoRepository: PhotoRepository,
    private val animalPhotoRepository: AnimalPhotoRepository,
    private val documentRepository: DocumentRepository,
    private val animalStatusLogRepository: AnimalStatusLogRepository,
    private val statusLogPhotoRepository: StatusLogPhotoRepository,
    private val statusLogDocumentRepository: StatusLogDocumentRepository,
    private val attributeValueHistoryRepository: AttributeValueHistoryRepository,
    private val s3Service: S3Service
) : AnimalService {

    @Transactional
    override fun createAnimal(username: String, animalDto: AnimalDto): Animal {
        val user = userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found")

        val animal = animalRepository.save(
            Animal(
                name = animalDto.name,
                description = animalDto.description,
                createdAt = LocalDate.now()
            )
        )

        animalUserRepository.save(
            AnimalUser(
                user = user,
                animal = animal
            )
        )

        animalDto.attributes.forEach { (name, value) ->
            val attribute = attributeRepository.save(
                Attribute(
                    name = name,
                    animal = animal
                )
            )

            valueRepository.save(
                Value(
                    value = value,
                    attribute = attribute
                )
            )
        }

        return animal
    }

    override fun getAnimalsByUser(username: String): List<Animal> {
        return animalUserRepository.findAllByUserUsername(username)
            .map { it.animal!! }
    }

    @Transactional
    override fun uploadAnimalPhoto(username: String, animalId: Long, file: MultipartFile): FileResponseDto {
        validateAnimalOwnership(username, animalId)

        val key = "animals/$animalId/photos/${UUID.randomUUID()}"
        val s3Key = s3Service.uploadFile(key, file)

        val photo = photoRepository.save(
            Photo(
                objectKey = s3Key,
                createdAt = LocalDate.now()
            )
        )

        animalPhotoRepository.save(
            AnimalPhoto(
                animal = animalRepository.getReferenceById(animalId),
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

    // Аналогичные методы для документов и статус-логов
    // ...

    private fun validateAnimalOwnership(username: String, animalId: Long) {
        if (!animalUserRepository.existsByUserUsernameAndAnimalId(username, animalId)) {
            throw AccessDeniedException("User doesn't have access to this animal")
        }
    }
}