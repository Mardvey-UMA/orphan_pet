package ru.animaltracker.authservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class BeansConfig (
    val userDetailsService: UserDetailsService
){

    @Bean
    fun authenticationProvider(): AuthenticationProvider{
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }
    @Bean
    fun authenticationManager(
        config: AuthenticationConfiguration
    ) : AuthenticationManager =
        config.getAuthenticationManager()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}