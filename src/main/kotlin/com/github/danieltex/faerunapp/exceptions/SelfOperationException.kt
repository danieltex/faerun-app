package com.github.danieltex.faerunapp.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Cannot make operation to itself")
class SelfOperationException : ResponseStatusException(HttpStatus.BAD_REQUEST,
    "Cannot make operation to itself")
