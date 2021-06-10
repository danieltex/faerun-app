package com.github.danieltex.faerunapp.dtos

import java.math.BigDecimal

class SettleOperationsDTO(
    val operations: Map<Int, List<OperationDetails>>,
    val storage: Map<Int, BigDecimal>
)
