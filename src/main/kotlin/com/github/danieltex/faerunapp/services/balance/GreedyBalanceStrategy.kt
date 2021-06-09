package com.github.danieltex.faerunapp.services.balance

import com.github.danieltex.faerunapp.entities.LoanEntity
import java.math.BigDecimal
import java.util.*

/**
 * A greedy implementation to reduce payment operations
 */
class GreedyBalanceStrategy(
    private val loans: Iterable<LoanEntity>
) {
    // create two priority queues, biggest absolute value first
    private val creditors = PriorityQueue<Balance>(Comparator.reverseOrder())
    private val debtors = PriorityQueue<Balance>()

    fun execute(): List<Operation> {

        filterCreditorsAndDebtors()

        // make biggest debtor and creditor pay each other
        // put them back on the list until their balance is zero
        // stores the operation on a list
        val operations = mutableListOf<Operation>()
        while (creditors.isNotEmpty()) {
            val biggestCreditor = creditors.poll()
            val biggestDebtor = debtors.poll()

            val quantity = biggestCreditor.balance.min(biggestDebtor.balance.abs())
            biggestCreditor.balance -= quantity
            biggestDebtor.balance += quantity
            if (biggestCreditor.balance > BigDecimal.ZERO) {
                creditors += biggestCreditor
            }
            if (biggestDebtor.balance < BigDecimal.ZERO) {
                debtors += biggestDebtor
            }
            operations += Operation(
                debtor = biggestDebtor.id,
                creditor = biggestCreditor.id,
                quantity = quantity
            )
        }

        return operations
    }

    // positive balance goes to creditors queue
    // negative balance goes to debtors queue
    private fun filterCreditorsAndDebtors() {
        val balanceMap: Map<Int, BigDecimal> = calculateBalanceMap(loans)

        balanceMap.forEach { (id, balance) ->
            val pair = Balance(id, balance)
            if (balance < BigDecimal.ZERO) {
                debtors += pair
            } else if (balance > BigDecimal.ZERO) {
                creditors += pair
            }
        }
    }

    // Maps how much each water pocket balance
    // positive value if should receive
    // negative value if should pay
    private fun calculateBalanceMap(loans: Iterable<LoanEntity>): Map<Int, BigDecimal> {
        val map = mutableMapOf<Int, BigDecimal>()

        loans.forEach { loan ->
            val creditorId = loan.id.creditor.id!!
            val debtorId = loan.id.debtor.id!!
            map[creditorId] = map.getOrDefault(creditorId, BigDecimal.ZERO) + loan.quantity
            map[debtorId] = map.getOrDefault(debtorId, BigDecimal.ZERO) - loan.quantity
        }

        return map
    }
}
