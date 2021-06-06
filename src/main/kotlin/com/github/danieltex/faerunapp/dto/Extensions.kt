package com.github.danieltex.faerunapp.dto

import com.github.danieltex.faerunapp.entities.WaterPocketEntity

fun WaterPocketEntity.toDTO(): WaterPocketDTO = WaterPocketDTO(id, name, storage)
fun WaterPocketDTO.toEntity(): WaterPocketEntity = WaterPocketEntity(id, name, storage)