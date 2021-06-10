package com.github.danieltex.faerunapp.dtos

import java.math.BigDecimal

data class BalanceDTO(
    val balance: Map<Int, WaterPocketBalance>
)

data class WaterPocketBalance(
    val storage: BigDecimal,
    val operations: MutableList<OperationDetails> = mutableListOf()
)
