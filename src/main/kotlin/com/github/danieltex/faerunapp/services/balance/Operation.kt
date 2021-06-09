package com.github.danieltex.faerunapp.services.balance

import java.math.BigDecimal

class Operation(
    val debtor: Int,
    val creditor: Int,
    val quantity: BigDecimal
)
