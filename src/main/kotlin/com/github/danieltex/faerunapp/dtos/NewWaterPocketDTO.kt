package com.github.danieltex.faerunapp.dtos

import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits
import javax.validation.constraints.NotBlank

class NewWaterPocketDTO(
    @field:NotBlank
    @field:Length(min = 3, message = "Should be at least 3 characters long")
    var name: String,
    @field:DecimalMin(value = "0.00")
    @field:Digits(integer = 19, fraction = 2)
    var storage: BigDecimal
)