package com.github.danieltex.faerunapp.repositories

import com.github.danieltex.faerunapp.entities.WaterPocket
import org.springframework.data.repository.PagingAndSortingRepository

interface WaterPocketRepository : PagingAndSortingRepository<WaterPocket, Int>
