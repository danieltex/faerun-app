package com.github.danieltex.faerunapp.services.impl

import com.github.danieltex.faerunapp.dtos.BalanceDTO
import com.github.danieltex.faerunapp.dtos.buildBalanceDTO
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
import com.github.danieltex.faerunapp.services.balance.GreedyBalanceStrategy
import com.github.danieltex.faerunapp.services.balance.Operation
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

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
        debtorId: Int,
        creditorId: Int,
        quantity: BigDecimal
    ): WaterPocketEntity {
        if (debtorId == creditorId) throw SelfOperationException()
        logger.info("New loan from creditor '{}' to  debtor '{}'", creditorId, debtorId)
        val debtor = findById(debtorId)
        val creditor = findById(creditorId)
        if (creditor.storage < quantity) throw InsufficientQuantityException(INSUFFICIENT_STORAGE_MESSAGE)

        // add to or create loan
        val loanId = LoanEntityId(creditor = creditor, debtor = debtor)
        val loan = loanRepository
            .findById(loanId)
            .orElse(LoanEntity(loanId, "0.00".toBigDecimal()))
        loan.quantity += quantity
        loanRepository.save(loan)

        // update water pockets storage
        creditor.storage -= quantity
        debtor.storage += quantity
        waterPocketRepository.save(creditor)
        waterPocketRepository.save(debtor)

        return debtor
    }

    override fun findAllDebts(id: Int): List<LoanEntity> {
        return loanRepository.findByIdDebtorId(id)
    }

    @Transactional
    override fun settle(
        debtorId: Int,
        creditorId: Int,
        quantity: BigDecimal
    ): WaterPocketEntity {
        if (creditorId == debtorId) throw SelfOperationException()
        logger.info("New payment from debtor '{}' to creditor '{}'", debtorId, creditorId)
        val creditor = findById(creditorId)
        val debtor = findById(debtorId)
        if (debtor.storage < quantity) throw InsufficientQuantityException(INSUFFICIENT_STORAGE_MESSAGE)

        // throw exception if payment is bigger than debt or no loan found
        val loanId = LoanEntityId(creditor = creditor, debtor = debtor)
        val loan = loanRepository
            .findById(loanId)
            .orElseThrow { InsufficientQuantityException(OVERPAYMENT_MESSAGE) }
        if (loan.quantity < quantity) throw InsufficientQuantityException(OVERPAYMENT_MESSAGE)

        // remove loan if fully payed
        if (loan.quantity.compareTo(quantity) == 0) {
            loanRepository.delete(loan)
        } else {
            loan.quantity -= quantity
            loanRepository.save(loan)
        }

        // update water pockets storage
        creditor.storage += quantity
        debtor.storage -= quantity
        waterPocketRepository.save(creditor)
        waterPocketRepository.save(debtor)

        return debtor
    }

    override fun getOptimizedBalance(): BalanceDTO {
        // retrieve all loans and optimize operations
        val allLoans = loanRepository.findAll()
        val optimizedOperations = GreedyBalanceStrategy(allLoans).execute()

        // retrieve all water-pockets involved in the optimized operations
        val waterPocketMap = retrieveWaterPockets(optimizedOperations)

        // map operations to balance dto
        return buildBalanceDTO(optimizedOperations, waterPocketMap)
    }

    private fun retrieveWaterPockets(optimizedOperations: List<Operation>): Map<Int, WaterPocketEntity> {
        val waterPocketIds = optimizedOperations
            .map { listOf(it.creditor, it.debtor) }
            .flatten()
            .toSet()
        return waterPocketRepository
            .findAllById(waterPocketIds)
            .associateBy { it.id!! }
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
