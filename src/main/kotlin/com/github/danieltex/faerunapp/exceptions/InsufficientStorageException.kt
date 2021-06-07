package com.github.danieltex.faerunapp.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Insufficient storage on water pocket")
class InsufficientStorageException : RuntimeException("Insufficient storage on water pocket")