package com.github.danieltex.faerunapp.controllers

import com.github.danieltex.faerunapp.entities.WaterPocket
import com.github.danieltex.faerunapp.services.WaterPocketService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/water-pockets")
class WaterPocketController(
    private val waterPocketService: WaterPocketService
) {

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createWaterPocket(@RequestBody waterPocket: WaterPocket): ResponseEntity<*> {
        val save = waterPocketService.save(waterPocket)
        return ResponseEntity.status(HttpStatus.CREATED).body(save)
    }
}
