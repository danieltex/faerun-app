package com.github.danieltex.faerunapp.services.impl

import com.github.danieltex.faerunapp.entities.WaterPocket
import com.github.danieltex.faerunapp.repositories.WaterPocketRepository
import com.github.danieltex.faerunapp.services.WaterPocketService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WaterPocketServiceImpl(
    private val waterPocketRepository: WaterPocketRepository
) : WaterPocketService {

    override fun save(waterPocket: WaterPocket): WaterPocket {
        val result = waterPocketRepository.save(waterPocket)
        logger.info("New water pocket: {}", result)
        return result
    }

    override fun findById(id: Int): WaterPocket? = waterPocketRepository
            .findById(id)
            .orElse(null)

    companion object {
        private val logger = LoggerFactory.getLogger(WaterPocketServiceImpl::class.java)
    }
}
