package com.github.sddisk.usernotes.handler

import com.github.sddisk.usernotes.exception.NoteNotFoundException
import com.github.sddisk.usernotes.exception.UserAlreadyExistsException
import com.github.sddisk.usernotes.exception.UserNotFoundException
import com.github.sddisk.usernotes.handler.response.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(ex: MethodArgumentNotValidException): ErrorResponse {
        val errors = ex.bindingResult
            .fieldErrors
            .map { fieldError ->
                "${fieldError.field}: $${fieldError.defaultMessage}"
            }

        val response = ErrorResponse(
            message = "Validation failed",
            details = errors,
            timestamp = LocalDateTime.now()
        )

        logEx(ex, ex.message, errors)

        return response
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUserAlreadyExists(ex: UserAlreadyExistsException): ErrorResponse {
        val message = ex.message ?: "User already exists"

        logEx(ex, message)

        return ErrorResponse(
            message = message,
            timestamp = LocalDateTime.now()
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUserNotFound(ex: UserNotFoundException): ErrorResponse {
        val message = ex.message ?: "User not found"

        logEx(ex, message)

        return ErrorResponse(
            message = message,
            timestamp = LocalDateTime.now()
        )
    }

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadCredentials(ex: BadCredentialsException): ErrorResponse {
        val message = ex.message ?: "Invalid credentials"

        logEx(ex, message)

        return ErrorResponse(
            message = message,
            timestamp = LocalDateTime.now()
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoteNotFound(ex: NoteNotFoundException): ErrorResponse {
        val message = ex.message ?: "Note not found"

        logEx(ex, message)

        return ErrorResponse(
            message = message,
            timestamp = LocalDateTime.now()
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ErrorResponse {
        val message = ex.message ?: "Bad JSON format"

        logEx(ex, message)

        return ErrorResponse(
            message = message,
            timestamp = LocalDateTime.now()
        )
    }

    @ExceptionHandler(MissingRequestCookieException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleMissingRequestCookie(ex: MissingRequestCookieException): ErrorResponse {
        val message = ex.message

        logEx(ex, message)

        return ErrorResponse(
            message = message,
            timestamp = LocalDateTime.now()
        )
    }

    private fun logEx(
        ex: Exception,
        message: String,
        details: List<String> = emptyList()
    ) = log.error("Handled exception: ${ex.javaClass.simpleName} | Message: $message | Details: $details")

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}