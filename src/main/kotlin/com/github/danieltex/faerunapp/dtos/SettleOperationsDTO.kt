package com.github.danieltex.faerunapp.dtos

import java.math.BigDecimal

data class SettleOperationsDTO(
    val operations: Map<Int, List<OperationDetails>>,
    val storage: Map<Int, BigDecimal>
)
