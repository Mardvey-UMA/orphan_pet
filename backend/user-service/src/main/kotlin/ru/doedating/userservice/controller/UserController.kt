package ru.doedating.userservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.doedating.userservice.repository.UserRepository

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepository: UserRepository
) {
    @GetMapping("/check")
    fun checkUserExists(@RequestHeader("X-User-Name") username: String?): ResponseEntity<Boolean> {
        val exists = username?.let { userRepository.findByUsername(it) } != null
        return ResponseEntity.ok(exists)
    }

    @GetMapping
    fun getUser(
        @RequestHeader("X-User-Name") username: String?
    ): ResponseEntity<Any> {
        if (username.isNullOrBlank()) {
            return ResponseEntity.badRequest().body(
                mapOf("error" to "Header X-User-Name is required")
            )
        }

        return ResponseEntity.ok(
            mapOf(
                "username" to username,
                "message" to "Hello, $username"
            )
        )
    }
}