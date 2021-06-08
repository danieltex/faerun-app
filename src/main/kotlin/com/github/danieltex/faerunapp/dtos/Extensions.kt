package com.github.danieltex.faerunapp.dtos

import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.WaterPocketEntity

fun WaterPocketEntity.toDTO(): WaterPocketDTO = WaterPocketDTO(name = name, storage = storage, id = id)

fun WaterPocketDTO.toEntity(): WaterPocketEntity = WaterPocketEntity(name = name, storage = storage, id = id)

fun Collection<LoanEntity>.toDTO(): DebitListDTO = DebitListDTO(debits = this.map(LoanEntity::toDTO))

fun LoanEntity.toDTO(): DebitDTO = DebitDTO(id = id.to.id!!, quantity = quantity)
