package com.github.danieltex.faerunapp.services.balance

import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import com.github.danieltex.faerunapp.entities.LoanEntity as Loan
import com.github.danieltex.faerunapp.entities.LoanEntityId as Id

class GreedyBalanceStrategyTest {
    @Test
    fun `Assert simplifies transitive debts`() {
        val alpha = WaterPocketEntity("Alpha", BigDecimal.ZERO, id = 1)
        val beta = WaterPocketEntity("Beta", BigDecimal.TEN, id = 2)
        val gamma = WaterPocketEntity("Gamma", BigDecimal.TEN, id = 3)

        //       10      10
        // gamma -> beta -> alpha
        val loans = listOf(
            Loan(Id(creditor = alpha, debtor = beta), BigDecimal.TEN),
            Loan(Id(creditor = beta, debtor = gamma), BigDecimal.TEN)
        )
        val result = GreedyBalanceStrategy(loans).execute()

        // expected:
        //      10
        //gamma -> alpha
        assertEquals(1, result.size)
        assertEquals(alpha.id, result[0].creditor)
        assertEquals(gamma.id, result[0].debtor)
        assertEquals(10.0, result[0].quantity.toDouble())
    }

    @Test
    fun `Assert zero balance water pockets are not included`() {
        val alpha = WaterPocketEntity("Alpha", BigDecimal.ZERO, id = 1)
        val beta = WaterPocketEntity("Beta", BigDecimal.TEN, id = 2)
        val gamma = WaterPocketEntity("Gamma", BigDecimal.TEN, id = 3)
        val loans = listOf(
            Loan(Id(creditor = alpha, debtor = beta), BigDecimal.TEN),
            Loan(Id(creditor = beta, debtor = gamma), BigDecimal.TEN),
            Loan(Id(creditor = gamma, debtor = alpha), BigDecimal.TEN)
        )
        val result = GreedyBalanceStrategy(loans).execute()

        assertEquals(0, result.size)
    }
}
