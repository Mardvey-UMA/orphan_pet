package ru.animaltracker.authservice.exception

import jakarta.mail.MessagingException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.animaltracker.authservice.enums.BusinessErrorCodes

@RestControllerAdvice
class GlobalExceptionHandler {

    class UserAlreadyExistsException(message: String) : RuntimeException(message)
    class InvalidTokenException(message: String) : RuntimeException(message)
    class UserNotFoundException(message: String) : RuntimeException(message)

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(exp: InvalidTokenException): ResponseEntity<ru.animaltracker.authservice.exception.ExceptionResponse> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ru.animaltracker.authservice.exception.ExceptionResponse(
                    businessErrorCode = ru.animaltracker.authservice.enums.BusinessErrorCodes.INVALID_TOKEN.code,
                    businessErrorDescription = ru.animaltracker.authservice.enums.BusinessErrorCodes.INVALID_TOKEN.description,
                    error = exp.message
                )
            )

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(exp: UserNotFoundException): ResponseEntity<ru.animaltracker.authservice.exception.ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ru.animaltracker.authservice.exception.ExceptionResponse(
                    businessErrorCode = ru.animaltracker.authservice.enums.BusinessErrorCodes.USER_NOT_FOUND.code,
                    businessErrorDescription = ru.animaltracker.authservice.enums.BusinessErrorCodes.USER_NOT_FOUND.description,
                    error = exp.message
                )
            )
    
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleException (exp: UserAlreadyExistsException): ResponseEntity<ru.animaltracker.authservice.exception.ExceptionResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ru.animaltracker.authservice.exception.ExceptionResponse(
                    businessErrorCode = ru.animaltracker.authservice.enums.BusinessErrorCodes.USER_ALREADY_EXISTS.code,
                    businessErrorDescription = ru.animaltracker.authservice.enums.BusinessErrorCodes.USER_ALREADY_EXISTS.description,
                    error = "User with this email / username already exists"
                )
            )

    @ExceptionHandler(LockedException::class)
    fun handleException (exp: LockedException): ResponseEntity<ru.animaltracker.authservice.exception.ExceptionResponse> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ru.animaltracker.authservice.exception.ExceptionResponse(
                    businessErrorCode = ru.animaltracker.authservice.enums.BusinessErrorCodes.ACCOUNT_LOCKED.code,
                    businessErrorDescription = ru.animaltracker.authservice.enums.BusinessErrorCodes.ACCOUNT_LOCKED.description,
                    error = exp.message
                )
            )
    @ExceptionHandler(DisabledException::class)
    fun handleException (exp: DisabledException): ResponseEntity<ru.animaltracker.authservice.exception.ExceptionResponse> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ru.animaltracker.authservice.exception.ExceptionResponse(
                    businessErrorCode = ru.animaltracker.authservice.enums.BusinessErrorCodes.ACCOUNT_DISABLED.code,
                    businessErrorDescription = ru.animaltracker.authservice.enums.BusinessErrorCodes.ACCOUNT_DISABLED.description,
                    error = exp.message
                )
            )

    @ExceptionHandler(BadCredentialsException::class)
    fun handleException (exp: BadCredentialsException): ResponseEntity<ru.animaltracker.authservice.exception.ExceptionResponse> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ru.animaltracker.authservice.exception.ExceptionResponse(
                    businessErrorCode = ru.animaltracker.authservice.enums.BusinessErrorCodes.BAD_CREDENTIALS.code,
                    businessErrorDescription = ru.animaltracker.authservice.enums.BusinessErrorCodes.BAD_CREDENTIALS.description,
                    error = ru.animaltracker.authservice.enums.BusinessErrorCodes.BAD_CREDENTIALS.description
                )
            )

    @ExceptionHandler(MessagingException::class)
    fun handleException (exp: MessagingException): ResponseEntity<ru.animaltracker.authservice.exception.ExceptionResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ru.animaltracker.authservice.exception.ExceptionResponse(
                    error = exp.message,
                )
            )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException (exp: MethodArgumentNotValidException): ResponseEntity<ru.animaltracker.authservice.exception.ExceptionResponse> {

        val errors: MutableSet<String> = HashSet()
        exp.bindingResult.fieldErrors.forEach { fieldError ->
            fieldError.defaultMessage?.let { errors.add(it) }
        }

        return         ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ru.animaltracker.authservice.exception.ExceptionResponse(
                    validationErrors = errors,
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException (exp: Exception): ResponseEntity<ru.animaltracker.authservice.exception.ExceptionResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ru.animaltracker.authservice.exception.ExceptionResponse(
                    businessErrorDescription = "Very bad(",
                    error = exp.message
                )
            )
}