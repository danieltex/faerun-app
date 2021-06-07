package com.github.danieltex.faerunapp.dto

import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits

data class WaterPocketDTO(
    var name: String,
    @DecimalMin(value = "0.00")
    @Digits(integer = 19, fraction = 2)
    var storage: BigDecimal,
    var id: Int? = null
)
