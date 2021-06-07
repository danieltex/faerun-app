package com.github.danieltex.faerunapp.controllers

import com.github.danieltex.faerunapp.exceptions.InsufficientStorageException
import com.github.danieltex.faerunapp.exceptions.WaterPocketNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(WaterPocketNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleWaterPocketNotFound(ex: RuntimeException): ResponseEntity<String> {
        logger.error("Handled exception", ex)
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InsufficientStorageException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInsufficientStorage(ex: RuntimeException): ResponseEntity<String> {
        logger.error("Handled exception", ex)
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun unknownException(ex: Throwable): ResponseEntity<String> {
        logger.error("Unhandled exception", ex)
        return ResponseEntity(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)
    }
}