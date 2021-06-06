package com.github.danieltex.faerunapp.controllers

import com.github.danieltex.faerunapp.dto.WaterPocketBatchDTO
import com.github.danieltex.faerunapp.dto.WaterPocketDTO
import com.github.danieltex.faerunapp.dto.toDTO
import com.github.danieltex.faerunapp.dto.toEntity
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import com.github.danieltex.faerunapp.services.WaterPocketService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    path = ["/water-pockets"],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class WaterPocketController(
    private val waterPocketService: WaterPocketService
) {

    @PostMapping
    fun createWaterPocket(@RequestBody waterPocket: WaterPocketDTO): ResponseEntity<*> {
        val created = waterPocketService.save(waterPocket.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping(consumes = [MediaType.ALL_VALUE])
    fun getWaterPocketBatch(): ResponseEntity<*> {
        val waterPockets = waterPocketService.findAll().map(WaterPocketEntity::toDTO)
        val batch = WaterPocketBatchDTO(waterPockets)
        return ResponseEntity.ok(batch);
    }

    @GetMapping("/{id}", consumes = [MediaType.ALL_VALUE])
    fun getWaterPocket(@PathVariable("id") id: Int): ResponseEntity<WaterPocketDTO> {
        val waterPocket = waterPocketService.findById(id)
        return if (waterPocket != null) {
            ResponseEntity.ok(waterPocket.toDTO())
        } else {
            ResponseEntity.notFound().build()

        }
    }
}
