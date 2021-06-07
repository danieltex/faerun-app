package com.github.danieltex.faerunapp.repositories

import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.LoanEntityId
import org.springframework.data.repository.PagingAndSortingRepository

interface LoanRepository : PagingAndSortingRepository<LoanEntity, LoanEntityId> {

    fun findByIdFrom(fromId: Int): List<LoanEntity>

    fun findByIdTo(toId: Int): List<LoanEntity>
}