package com.github.danieltex.faerunapp.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class WaterPocketNotFoundException(id: Int) : RuntimeException("Water Pocket '$id' not found")
