package com.github.danieltex.faerunapp.dtos

import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import com.github.danieltex.faerunapp.services.balance.Operation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ExtensionsKtTest {
    @Test
    fun `should convert operation list to balance`() {
        val operations = listOf(
            Operation(debtor = 1, creditor = 2, BigDecimal.TEN),
            Operation(debtor = 5, creditor = 2, BigDecimal.ONE),
        )
        val waterPockets = mapOf(
            1 to WaterPocketEntity("1", "100".toBigDecimal(), 1),
            2 to WaterPocketEntity("2", "200".toBigDecimal(), 2),
            5 to WaterPocketEntity("5", "500".toBigDecimal(), 5),
        )
        val expected = BalanceDTO(
            mapOf(
                1 to WaterPocketBalance(
                    "100".toBigDecimal(),
                    mutableListOf(
                        OperationDetails(
                            OperationType.PAY,
                            2,
                            BigDecimal.TEN
                        )
                    )
                ),
                5 to WaterPocketBalance(
                    "500".toBigDecimal(),
                    mutableListOf(
                        OperationDetails(
                            OperationType.PAY,
                            2,
                            BigDecimal.ONE
                        )
                    )
                ),
                2 to WaterPocketBalance(
                    "200".toBigDecimal(),
                    mutableListOf(
                        OperationDetails(
                            OperationType.RECEIVE,
                            1,
                            BigDecimal.TEN
                        ),
                        OperationDetails(
                            OperationType.RECEIVE,
                            5,
                            BigDecimal.ONE
                        )
                    )
                ),
            )
        )

        val result = buildBalanceDTO(operations, waterPockets)

        assertEquals(expected, result)
    }
}
