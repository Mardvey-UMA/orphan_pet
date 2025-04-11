package ru.animaltracker.authservice.service.interfaces

import ru.animaltracker.authservice.dto.UserRequestDTO
import ru.animaltracker.authservice.dto.UserResponseDTO
import ru.animaltracker.authservice.entity.Role
import ru.animaltracker.authservice.entity.User
import ru.animaltracker.authservice.exception.GlobalExceptionHandler

interface UserService {

    fun saveUser(user: User): User
    fun saveRole(role: Role): Role
    fun getUsers(): List<User>

    @Throws(GlobalExceptionHandler.UserAlreadyExistsException::class, IllegalStateException::class)
    fun registerUser(email: String, username: String, rawPassword: String): User

    @Throws(GlobalExceptionHandler.UserAlreadyExistsException::class, IllegalStateException::class)
    fun registerVkUser(userRequest: UserRequestDTO, vkId: Long): User

    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
    fun findById(id: Long): User?
    fun findByVkId(vkId: Long): User?
    fun enableUser(user: User)
    fun updatePassword(user: User, newPassword: String)
}
