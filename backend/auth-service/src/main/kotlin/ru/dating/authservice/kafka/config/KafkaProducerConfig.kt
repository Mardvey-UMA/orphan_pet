package ru.dating.authservice.kafka.config

import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import ru.dating.authservice.config.KafkaConfigProps

@Configuration
class KafkaProducerConfig(
    private val kafkaProperties: KafkaConfigProps
) {
    @Bean
    fun kafkaProducerFactory(props: KafkaConfigProps): ProducerFactory<String, Any> {
        val config = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to props.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to props.producer.keySerializer,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to props.producer.valueSerializer
        )
        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> =
        KafkaTemplate(kafkaProducerFactory(kafkaProperties))
}