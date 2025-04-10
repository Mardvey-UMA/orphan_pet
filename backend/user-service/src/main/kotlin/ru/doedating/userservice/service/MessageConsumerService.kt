package ru.doedating.userservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import ru.doedating.userservice.dto.UserCreatedEvent
import ru.doedating.userservice.entity.User
import ru.doedating.userservice.repository.UserRepository

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
        userRepository.findByEmail(gitler.email)
            ?: userRepository.save(gitler)
        return
    }
}