package ru.animaltracker.userservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import ru.animaltracker.userservice.dto.UserCreatedEvent
import ru.animaltracker.userservice.entity.User
import ru.animaltracker.userservice.repository.UserRepository
@Service
class MessageConsumerService(
    private val objectMapper: ObjectMapper,
    private val userRepository: UserRepository,
) {

    @KafkaListener(topics = ["user-created"])
    fun listen(json: String) {
        val dto = objectMapper.readValue(json, UserCreatedEvent::class.java)
        val gitler = User(
            email = dto.email,
            username = dto.username,
        )
        gitler.email?.let { userRepository.findByEmail(it) }
            ?: userRepository.save(gitler)
        return
    }
}