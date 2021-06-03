package com.github.danieltex.faerunapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FaerunAppApplication

fun main(args: Array<String>) {
	runApplication<FaerunAppApplication>(*args)
}
