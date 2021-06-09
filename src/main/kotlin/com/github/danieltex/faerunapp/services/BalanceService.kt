package com.github.danieltex.faerunapp.services

import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.services.balance.Operation

interface BalanceService {
    fun resolve(loans: List<LoanEntity>): List<Operation>
}
