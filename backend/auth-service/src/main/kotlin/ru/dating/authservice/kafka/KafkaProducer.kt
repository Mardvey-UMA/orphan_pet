package ru.dating.authservice.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) {
    fun sendMessage(topic: String, payload: Any) {
        kafkaTemplate.send(topic, payload)
    }
}