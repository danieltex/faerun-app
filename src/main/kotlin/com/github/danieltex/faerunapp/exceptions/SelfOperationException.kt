package com.github.danieltex.faerunapp.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Cannot make operation to itself")
class SelfOperationException : RuntimeException("Cannot make operation to itself")
