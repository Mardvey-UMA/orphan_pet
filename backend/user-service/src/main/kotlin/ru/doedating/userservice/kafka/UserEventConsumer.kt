package ru.doedating.userservice.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import ru.doedating.userservice.dto.UserCreatedEvent
import ru.doedating.userservice.entity.User
import ru.doedating.userservice.repository.UserRepository

@Service
class UserEventConsumer(
    private val userRepository: UserRepository
) {
    @KafkaListener(topics = ["user-created"], groupId = "user-service-group")
    fun handleUserCreated(event: UserCreatedEvent) {
        val user = User(
            email = event.email,
            username = event.username,
        )
        userRepository.save(user)
    }
}