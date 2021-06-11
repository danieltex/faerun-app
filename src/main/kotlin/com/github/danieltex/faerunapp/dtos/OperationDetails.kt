package com.github.danieltex.faerunapp.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.math.BigDecimal

data class OperationDetails(
    val operation: OperationType,
    @JsonProperty("destination-id")
    val destinationId: Int,
    val quantity: BigDecimal
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OperationDetails) return false

        if (operation != other.operation) return false
        if (destinationId != other.destinationId) return false
        if (quantity.compareTo(other.quantity) != 0) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operation.hashCode()
        result = 31 * result + destinationId
        result = 31 * result + quantity.hashCode()
        return result
    }
}

enum class OperationType(@get:JsonValue val opName: String) {
    PAY("pay"),
    RECEIVE("receive");
}
