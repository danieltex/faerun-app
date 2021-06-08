package com.github.danieltex.faerunapp.controllers

import com.github.danieltex.faerunapp.exceptions.InsufficientQuantityException
import com.github.danieltex.faerunapp.exceptions.SelfOperationException
import com.github.danieltex.faerunapp.exceptions.WaterPocketNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(WaterPocketNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleWaterPocketNotFound(ex: RuntimeException): ResponseEntity<String> {
        logger.error(Companion.HANDLED_EXCEPTION, ex)
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InsufficientQuantityException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInsufficientStorage(ex: RuntimeException): ResponseEntity<String> {
        logger.error(Companion.HANDLED_EXCEPTION, ex)
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(SelfOperationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleSelfOperationException(ex: RuntimeException): ResponseEntity<String> {
        logger.error(Companion.HANDLED_EXCEPTION, ex)
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun unknownException(ex: Throwable): ResponseEntity<String> {
        logger.error(UNHANDLED_EXCEPTION, ex)
        return ResponseEntity(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<String> {
        return ResponseEntity("Malformed request body. Check de API documentation", HttpStatus.BAD_REQUEST)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)
        private const val HANDLED_EXCEPTION = "Handled exception"
        private const val UNHANDLED_EXCEPTION = "Unhandled exception"

    }
}
