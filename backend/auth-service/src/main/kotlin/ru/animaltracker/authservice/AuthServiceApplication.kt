package ru.animaltracker.authservice

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import ru.animaltracker.authservice.config.EmailConfig
import ru.animaltracker.authservice.config.JwtConfig
import ru.animaltracker.authservice.config.KafkaConfigProps
import ru.animaltracker.authservice.entity.Role
import ru.animaltracker.authservice.repository.RoleRepository

@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(ru.animaltracker.authservice.config.JwtConfig::class, EmailConfig::class, KafkaConfigProps::class)
class AuthServiceApplication{
    @Bean
    fun runner(roleRepository: RoleRepository): CommandLineRunner {
        return CommandLineRunner {
            if (roleRepository.findByName("USER") == null) {
                roleRepository.save(Role(name = "USER"))
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
