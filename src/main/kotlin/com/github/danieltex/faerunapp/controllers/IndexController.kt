package com.github.danieltex.faerunapp.controllers

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
class IndexController {
    @GetMapping("/")
    fun message() = "Faerun Water-Pocket balance API"
}
