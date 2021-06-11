package com.github.danieltex.faerunapp.services

import com.github.danieltex.faerunapp.dtos.BalanceDTO
import com.github.danieltex.faerunapp.dtos.SettleOperationsDTO
import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import java.math.BigDecimal

interface WaterPocketService {
    fun save(waterPocket: WaterPocketEntity): WaterPocketEntity
    fun loan(debtorId: Int, creditorId: Int, quantity: BigDecimal): WaterPocketEntity
    fun settle(debtorId: Int, creditorId: Int, quantity: BigDecimal): WaterPocketEntity
    fun findAll(): List<WaterPocketEntity>
    fun findAllDebts(id: Int): List<LoanEntity>
    fun findById(id: Int): WaterPocketEntity
    fun getOptimizedBalance(): BalanceDTO
    fun settleAll(): SettleOperationsDTO
}
