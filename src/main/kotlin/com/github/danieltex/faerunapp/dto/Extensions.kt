package com.github.danieltex.faerunapp.dto

import com.github.danieltex.faerunapp.entities.WaterPocketEntity

fun WaterPocketEntity.toDTO(): WaterPocketDTO = WaterPocketDTO(name = name, storage = storage, id = id)
fun WaterPocketDTO.toEntity(): WaterPocketEntity = WaterPocketEntity(name = name, storage = storage, id = id)
