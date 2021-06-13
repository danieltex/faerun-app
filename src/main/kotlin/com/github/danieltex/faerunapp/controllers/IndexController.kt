package com.github.danieltex.faerunapp.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController {
    @GetMapping("/")
    fun message() = "Faerun Water-Pocket balance API"
}
