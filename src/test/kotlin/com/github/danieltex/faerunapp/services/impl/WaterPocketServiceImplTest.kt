package com.github.danieltex.faerunapp.services.impl

import com.github.danieltex.faerunapp.dto.LoanRequestDTO
import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.LoanEntityId
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import com.github.danieltex.faerunapp.exceptions.InsufficientStorageException
import com.github.danieltex.faerunapp.exceptions.WaterPocketNotFoundException
import com.github.danieltex.faerunapp.repositories.LoanRepository
import com.github.danieltex.faerunapp.repositories.WaterPocketRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class WaterPocketServiceImplTest {

    private val waterPocketRepository = mock<WaterPocketRepository>()
    private val loanRepository = mock<LoanRepository>()

    private val waterPocketService = WaterPocketServiceImpl(
        waterPocketRepository,
        loanRepository
    )

    @Test
    internal fun `when FROM or TO water-pockets doesn't exist should throw Not Found exception`() {
        val existingId = 123
        val nonExistingId456 = 456
        val nonExistingId789 = 789
        whenever(waterPocketRepository.findById(existingId)).thenReturn(Optional.of(
            newWaterPocket(existingId)
        ))

        val toException = assertThrows<WaterPocketNotFoundException> {
            waterPocketService.loan(existingId, loanRequestDTO(nonExistingId456))
        }
        val fromException = assertThrows<WaterPocketNotFoundException> {
            waterPocketService.loan(nonExistingId789, loanRequestDTO(existingId))
        }

        assertEquals("Water Pocket '456' not found", toException.message)
        assertEquals("Water Pocket '789' not found", fromException.message)
    }

    @Test
    internal fun `when quantity requested is bigger than storage should throw Insufficient Storage Exception`() {
        val to = 123
        val from = 456
        whenever(waterPocketRepository.findById(to)).thenReturn(Optional.of(
            newWaterPocket(to, "100.00")
        ))
        whenever(waterPocketRepository.findById(from)).thenReturn(Optional.of(
            newWaterPocket(from)
        ))

        assertThrows<InsufficientStorageException> {
            waterPocketService.loan(to, loanRequestDTO(from, "200"))
        }
    }

    @Test
    internal fun `when valid loan request should return updated storage and save new loan`() {
        val to = 123
        val from = 456
        val toWaterPocket = newWaterPocket(to, "toWP", "0.00")
        val fromWaterPocket = newWaterPocket(from, "fromWP", "100.00")
        whenever(waterPocketRepository.findById(to)).thenReturn(
            Optional.of(toWaterPocket)
        )
        whenever(waterPocketRepository.findById(from)).thenReturn(
            Optional.of(fromWaterPocket)
        )

        val result = waterPocketService.loan(to, loanRequestDTO(from, "100"))

        assertEquals(100.0, result.storage.toDouble())
        assertEquals(to, result.id)

        val newLoan = LoanEntity(LoanEntityId(fromWaterPocket, toWaterPocket), "100.00".toBigDecimal())
        verify(loanRepository).save(newLoan)
        verify(waterPocketRepository).save(newWaterPocket(to, "fromWP", "0.00"))
    }

    private fun loanRequestDTO(
        fromId: Int,
        quantity: String = "10"
    ) = LoanRequestDTO(fromId, quantity.toBigDecimal())

    private fun newWaterPocket(
        id: Int,
        name: String = "existing",
        storage: String = "10"
    ) = WaterPocketEntity(name, storage.toBigDecimal(), id)

}