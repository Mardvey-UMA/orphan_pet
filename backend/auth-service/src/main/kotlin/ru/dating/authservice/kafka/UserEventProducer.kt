package ru.dating.authservice.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import ru.dating.authservice.dto.UserCreatedEvent
import ru.dating.authservice.entity.User

@Service
class UserEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, UserCreatedEvent>
) {
    fun sendUserCreatedEvent(user: User) {
        val event = UserCreatedEvent(
            username = user.username,
            email = user.email,
        )
        kafkaTemplate.send("user-created", event)
    }
}