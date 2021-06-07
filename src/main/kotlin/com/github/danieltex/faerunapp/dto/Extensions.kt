package com.github.danieltex.faerunapp.dto

import com.github.danieltex.faerunapp.entities.WaterPocketEntity

fun WaterPocketEntity.toDTO(): WaterPocketDTO = WaterPocketDTO(name, storage, id)
fun WaterPocketDTO.toEntity(): WaterPocketEntity = WaterPocketEntity(name, storage, id)
