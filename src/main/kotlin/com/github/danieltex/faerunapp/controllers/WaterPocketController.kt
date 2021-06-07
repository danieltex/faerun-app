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
import org.springframework.web.bind.annotation.ResponseStatus
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
    @ResponseStatus(HttpStatus.CREATED)
    fun createWaterPocket(@RequestBody waterPocket: WaterPocketDTO): WaterPocketDTO {
        val created = waterPocketService.save(waterPocket.toEntity())
        return created.toDTO()
    }

    @GetMapping(consumes = [MediaType.ALL_VALUE])
    fun getWaterPocketBatch(): WaterPocketBatchDTO {
        val waterPockets = waterPocketService.findAll().map(WaterPocketEntity::toDTO)
        return WaterPocketBatchDTO(waterPockets)
    }

    @GetMapping("/{id}", consumes = [MediaType.ALL_VALUE])
    fun getWaterPocket(@PathVariable("id") id: Int): WaterPocketDTO {
        return waterPocketService.findById(id).toDTO()
    }

        }
    }
}
