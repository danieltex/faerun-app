package com.github.danieltex.faerunapp.controllers

import com.github.danieltex.faerunapp.dto.*
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import com.github.danieltex.faerunapp.services.WaterPocketService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

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

    @PostMapping("/{id}/borrow")
    fun borrow(
        @PathVariable("id") toWaterPocketId: Int,
        @RequestBody loanRequest: LoanRequestDTO
    ): WaterPocketDTO {
        return waterPocketService.loan(toWaterPocketId, loanRequest).toDTO()
    }
}
