package com.github.danieltex.faerunapp.dto

import java.math.BigDecimal

data class DebitListDTO(
    val debits: List<DebitDTO>
)

data class DebitDTO(
    val id: Int,
    val quantity: BigDecimal
)
