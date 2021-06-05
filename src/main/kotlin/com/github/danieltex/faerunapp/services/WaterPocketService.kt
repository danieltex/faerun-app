package com.github.danieltex.faerunapp.services

import com.github.danieltex.faerunapp.entities.WaterPocket

interface WaterPocketService {
    fun save(waterPocket: WaterPocket): WaterPocket
    fun findById(id: Int): WaterPocket?
}
