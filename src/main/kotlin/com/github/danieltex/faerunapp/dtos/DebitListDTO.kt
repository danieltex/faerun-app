package com.github.danieltex.faerunapp.dtos

import java.math.BigDecimal

data class DebitListDTO(
    val debts: List<DebitDTO>
)

data class DebitDTO(
    val id: Int,
    val quantity: BigDecimal
)
