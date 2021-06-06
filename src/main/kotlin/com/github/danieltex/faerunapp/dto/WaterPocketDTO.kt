package com.github.danieltex.faerunapp.dto

import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits

data class WaterPocketDTO(
    var id: Int?,
    var name: String,
    @DecimalMin(value = "0.00")
    @Digits(integer = 10, fraction = 2)
    var storage: BigDecimal
)