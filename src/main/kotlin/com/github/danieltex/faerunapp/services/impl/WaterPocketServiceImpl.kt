package com.github.danieltex.faerunapp.services.impl

import com.github.danieltex.faerunapp.dto.LoanRequestDTO
import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.LoanEntityId
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import com.github.danieltex.faerunapp.exceptions.InsufficientStorageException
import com.github.danieltex.faerunapp.exceptions.WaterPocketNotFoundException
import com.github.danieltex.faerunapp.repositories.LoanRepository
import com.github.danieltex.faerunapp.repositories.WaterPocketRepository
import com.github.danieltex.faerunapp.services.WaterPocketService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WaterPocketServiceImpl(
    private val waterPocketRepository: WaterPocketRepository,
    private val loanRepository: LoanRepository
) : WaterPocketService {

    override fun save(waterPocket: WaterPocketEntity): WaterPocketEntity {
        val result = waterPocketRepository.save(waterPocket)
        logger.info("New water pocket: {}", result)
        return result
    }

    @Transactional
    override fun loan(
        toWaterPocketId: Int,
        loanRequest: LoanRequestDTO
    ): WaterPocketEntity {
        logger.info("New loan from '{}' to '{}'", loanRequest.from, toWaterPocketId)
        // validate request
        val to = findById(toWaterPocketId)
        val from = findById(loanRequest.from)
        if (from.storage < loanRequest.quantity) throw InsufficientStorageException()

        // add to or create loan
        val loanId = LoanEntityId(from, to)
        val loan = loanRepository
            .findById(loanId)
            .orElse(LoanEntity(loanId, "0.00".toBigDecimal()))
        loan.quantity += loanRequest.quantity
        loanRepository.save(loan)

        // update water pockets storage
        from.storage -= loanRequest.quantity
        to.storage += loanRequest.quantity
        waterPocketRepository.save(from)
        waterPocketRepository.save(to)

        return to
    }

    override fun findById(id: Int): WaterPocketEntity = waterPocketRepository
        .findById(id)
        .orElseThrow { WaterPocketNotFoundException(id) }

    override fun findAll(): List<WaterPocketEntity> {
        return waterPocketRepository.findAll().toList()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WaterPocketServiceImpl::class.java)
    }
}
