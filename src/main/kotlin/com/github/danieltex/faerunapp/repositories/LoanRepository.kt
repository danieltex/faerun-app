package com.github.danieltex.faerunapp.repositories

import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.LoanEntityId
import org.springframework.data.repository.PagingAndSortingRepository

interface LoanRepository : PagingAndSortingRepository<LoanEntity, LoanEntityId> {

    /**
     * Return all loans made FROM a given water pocket
     */
    fun findByIdFromId(fromId: Int): List<LoanEntity>

    /**
     * Return all loans made TO a given water pocket
     */
    fun findByIdToId(toId: Int): List<LoanEntity>
}
