package com.github.danieltex.faerunapp.services.impl

import com.github.danieltex.faerunapp.dtos.LoanRequestDTO
import com.github.danieltex.faerunapp.dtos.PaymentRequestDTO
import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.LoanEntityId
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import com.github.danieltex.faerunapp.exceptions.InsufficientQuantityException
import com.github.danieltex.faerunapp.exceptions.InsufficientQuantityException.Companion.INSUFFICIENT_STORAGE_MESSAGE
import com.github.danieltex.faerunapp.exceptions.InsufficientQuantityException.Companion.OVERPAYMENT_MESSAGE
import com.github.danieltex.faerunapp.exceptions.SelfOperationException
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
        if (toWaterPocketId == loanRequest.from) throw SelfOperationException()
        logger.info("New loan from '{}' to '{}'", loanRequest.from, toWaterPocketId)
        val to = findById(toWaterPocketId)
        val from = findById(loanRequest.from)
        if (from.storage < loanRequest.quantity) throw InsufficientQuantityException(INSUFFICIENT_STORAGE_MESSAGE)

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

    override fun findAllLoansTo(id: Int): List<LoanEntity> {
        return loanRepository.findByIdToId(id)
    }

    @Transactional
    override fun settle(fromWaterPocketId: Int, paymentRequest: PaymentRequestDTO): WaterPocketEntity {
        if (fromWaterPocketId == paymentRequest.to) throw SelfOperationException()
        logger.info("New payment from '{}' to '{}'", fromWaterPocketId, paymentRequest.to)
        val to = findById(paymentRequest.to)
        val from = findById(fromWaterPocketId)
        if (from.storage < paymentRequest.quantity) throw InsufficientQuantityException(INSUFFICIENT_STORAGE_MESSAGE)

        // throw exception if payment is bigger than debt or no loan found
        val loanId = LoanEntityId(from, to)
        val loan = loanRepository
            .findById(loanId)
            .orElseThrow { InsufficientQuantityException(OVERPAYMENT_MESSAGE) }
        if (loan.quantity < paymentRequest.quantity) throw InsufficientQuantityException(OVERPAYMENT_MESSAGE)

        // remove loan if fully payed
        if (loan.quantity.compareTo(paymentRequest.quantity) == 0) {
            loanRepository.delete(loan)
        } else {
            loan.quantity -= paymentRequest.quantity
            loanRepository.save(loan)
        }

        // update water pockets storage
        from.storage -= paymentRequest.quantity
        to.storage += paymentRequest.quantity
        waterPocketRepository.save(from)
        waterPocketRepository.save(to)

        return from
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
