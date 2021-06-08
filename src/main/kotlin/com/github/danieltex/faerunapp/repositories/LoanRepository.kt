package com.github.danieltex.faerunapp.repositories

import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.LoanEntityId
import org.springframework.data.repository.PagingAndSortingRepository

interface LoanRepository : PagingAndSortingRepository<LoanEntity, LoanEntityId> {

    /**
     * Return all loans made from a creditor water pocket
     */
    fun findByIdCreditorId(id: Int): List<LoanEntity>

    /**
     * Return all loans made to a debtor water pocket
     */
    fun findByIdDebtorId(id: Int): List<LoanEntity>
}
