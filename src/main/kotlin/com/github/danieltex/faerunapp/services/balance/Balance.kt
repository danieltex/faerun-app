package com.github.danieltex.faerunapp.services.balance

import java.math.BigDecimal

internal class Balance(
    val id: Int,
    var balance: BigDecimal
) : Comparable<Balance> {
    override fun compareTo(other: Balance): Int {
        return balance.compareTo(other.balance)
    }
}
