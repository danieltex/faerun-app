package com.github.danieltex.faerunapp.controllers

import com.github.danieltex.faerunapp.entities.WaterPocket
import com.github.danieltex.faerunapp.services.WaterPocketService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/water-pockets")
class WaterPocketController(
    private val waterPocketService: WaterPocketService
) {

    @PostMapping
    fun addWaterPocket(@RequestBody waterPocket: WaterPocket): WaterPocket {
        return waterPocketService.save(waterPocket)
    }
}
