package ru.doedating.authservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "spring.kafka")
data class KafkaConfigProps(
    val bootstrapServers: String = "",
    val producer: ProducerProps = ProducerProps(),
    val consumer: ConsumerProps = ConsumerProps()
) {
    data class ProducerProps(
        val keySerializer: String = "",
        val valueSerializer: String = ""
    )

    data class ConsumerProps(
        val keyDeserializer: String = "",
        val valueDeserializer: String = "",
        val groupId: String = "",
        val autoOffsetReset: String = "",
        val properties: Map<String, String> = emptyMap()
    )
}