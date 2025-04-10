package ru.doedating.userservice.service.interfaces

import ru.doedating.userservice.entity.User

interface UserService {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
}