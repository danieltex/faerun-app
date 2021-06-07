package com.github.danieltex.faerunapp.dto

import java.math.BigDecimal
import javax.validation.constraints.DecimalMin

data class LoanRequestDTO(
    val from: Int,
    @DecimalMin("0.00", inclusive = false)
    val quantity: BigDecimal
)
