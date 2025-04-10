package ru.doedating.authservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class MessageProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    fun sendMessage(topic: String, dto: Any) {
        val json = objectMapper.writeValueAsString(dto)
        kafkaTemplate.send(topic, json)
    }
}