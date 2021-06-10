package com.github.danieltex.faerunapp.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@ResponseStatus(HttpStatus.NOT_FOUND)
class WaterPocketNotFoundException(id: Int) : ResponseStatusException(
    HttpStatus.NOT_FOUND,
    "Water Pocket '$id' not found"
)
