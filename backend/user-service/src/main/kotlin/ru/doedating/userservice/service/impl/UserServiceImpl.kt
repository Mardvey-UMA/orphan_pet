package ru.doedating.userservice.service.impl

import ru.doedating.userservice.entity.User
import ru.doedating.userservice.repository.UserRepository
import ru.doedating.userservice.service.interfaces.UserService

class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {
    override fun findByUsername(username: String): User? =
        userRepository.findByEmail(username)

    override fun findByEmail(email: String): User? =
        userRepository.findByEmail(email)
}