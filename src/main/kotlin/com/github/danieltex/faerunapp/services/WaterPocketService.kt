package com.github.danieltex.faerunapp.services

import com.github.danieltex.faerunapp.entities.WaterPocketEntity

interface WaterPocketService {
    fun save(waterPocket: WaterPocketEntity): WaterPocketEntity
    fun findById(id: Int): WaterPocketEntity?
    fun findAll(): List<WaterPocketEntity>
}
