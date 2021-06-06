package com.github.danieltex.faerunapp.repositories

import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface WaterPocketRepository : PagingAndSortingRepository<WaterPocketEntity, Int>
