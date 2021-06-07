package com.github.danieltex.faerunapp.dto

import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits
import javax.validation.constraints.NotBlank

data class WaterPocketDTO(
    @NotBlank
    @Length(min = 3, message = "Should be at least 3 characters long")
    var name: String,
    @DecimalMin(value = "0.00")
    @Digits(integer = 19, fraction = 2)
    var storage: BigDecimal,
    var id: Int? = null
)
