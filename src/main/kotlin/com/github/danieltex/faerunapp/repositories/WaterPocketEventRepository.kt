package com.github.danieltex.faerunapp.repositories

import com.github.danieltex.faerunapp.entities.WaterPocketEventEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface WaterPocketEventRepository: PagingAndSortingRepository<WaterPocketEventEntity, Long> {
    fun findByOriginIdOrderByDateDesc(id: Int): List<WaterPocketEventEntity>
}
