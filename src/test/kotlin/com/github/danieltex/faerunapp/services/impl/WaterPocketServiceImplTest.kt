package com.github.danieltex.faerunapp.services.impl

import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.LoanEntityId
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import com.github.danieltex.faerunapp.exceptions.InsufficientQuantityException
import com.github.danieltex.faerunapp.exceptions.SelfOperationException
import com.github.danieltex.faerunapp.exceptions.WaterPocketNotFoundException
import com.github.danieltex.faerunapp.repositories.LoanRepository
import com.github.danieltex.faerunapp.repositories.WaterPocketEventRepository
import com.github.danieltex.faerunapp.repositories.WaterPocketRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.*

class WaterPocketServiceImplTest {

    private val waterPocketRepository = mock<WaterPocketRepository>()
    private val loanRepository = mock<LoanRepository>()
    private val eventRepository = mock<WaterPocketEventRepository>()
    private val waterPocketService = WaterPocketServiceImpl(
        waterPocketRepository,
        loanRepository,
        eventRepository
    )

    @Test
    fun `when loan request FROM or TO water-pockets doesn't exist should throw Not Found exception`() {
        val existingId = 123
        val nonExistingId456 = 456
        val nonExistingId789 = 789
        whenever(waterPocketRepository.findById(existingId)).thenReturn(Optional.of(
            newWaterPocket(existingId)
        ))

        val toException = assertThrows<WaterPocketNotFoundException> {
            waterPocketService.loan(existingId, nonExistingId456, BigDecimal.TEN)
        }
        val fromException = assertThrows<WaterPocketNotFoundException> {
            waterPocketService.loan(nonExistingId789, existingId, BigDecimal.TEN)
        }

        assertEquals("Water Pocket '456' not found", toException.reason)
        assertEquals("Water Pocket '789' not found", fromException.reason)
    }

    @Test
    fun `when requested loan quantity is bigger than storage should throw Insufficient Quantity Exception`() {
        val debtor = 123
        val creditor = 456
        whenever(waterPocketRepository.findById(debtor)).thenReturn(Optional.of(
            newWaterPocket(debtor, "100.00")
        ))
        whenever(waterPocketRepository.findById(creditor)).thenReturn(Optional.of(
            newWaterPocket(creditor)
        ))

        val exception = assertThrows<InsufficientQuantityException> {
            waterPocketService.loan(debtor, creditor, "200".toBigDecimal())
        }
        assertEquals("Insufficient storage on water pocket", exception.reason)
    }

    @Test
    fun `when valid loan request should return updated storage and save loan and water pocket storages`() {
        val debtorId = 123
        val creditorId = 456
        val debtor = newWaterPocket(debtorId, "0.00")
        val creditor = newWaterPocket(creditorId, "100.00")
        whenever(waterPocketRepository.findById(debtorId)).thenReturn(
            Optional.of(debtor)
        )
        whenever(waterPocketRepository.findById(creditorId)).thenReturn(
            Optional.of(creditor)
        )

        // act
        val debtorResult = waterPocketService.loan(debtorId, creditorId, "100".toBigDecimal())

        // verify result
        assertEquals(100.0, debtorResult.storage.toDouble())
        assertEquals(debtorId, debtorResult.id)

        // verify new loan value
        val loanCaptor = argumentCaptor<LoanEntity>()
        verify(loanRepository).save(loanCaptor.capture())
        assertEquals(100.0, loanCaptor.firstValue.quantity.toDouble())

        // verify saved updated storage values
        val wpCaptor = argumentCaptor<WaterPocketEntity>()
        verify(waterPocketRepository, times(2)).save(wpCaptor.capture())
        val savedDebtor = wpCaptor.allValues.find { it.id == debtorId }!!
        val savedCreditor = wpCaptor.allValues.find { it.id == creditorId }!!
        assertEquals(100.0, savedDebtor.storage.toDouble())
        assertEquals(0.0, savedCreditor.storage.toDouble())
    }

    @Test
    fun `when payment FROM or TO water-pockets doesn't exist should throw Not Found exception`() {
        val existingId = 123
        val nonExistingId456 = 456
        val nonExistingId789 = 789
        whenever(waterPocketRepository.findById(existingId)).thenReturn(Optional.of(
            newWaterPocket(existingId)
        ))

        val toException = assertThrows<WaterPocketNotFoundException> {
            waterPocketService.settle(existingId, nonExistingId456, BigDecimal.TEN)
        }
        val fromException = assertThrows<WaterPocketNotFoundException> {
            waterPocketService.settle(nonExistingId789, existingId, BigDecimal.TEN)
        }

        assertEquals("Water Pocket '456' not found", toException.reason)
        assertEquals("Water Pocket '789' not found", fromException.reason)
    }

    @Test
    fun `when payment quantity is bigger than storage should throw Insufficient Quantity Exception`() {
        val creditorId = 123
        val debtorId = 456
        val creditor = newWaterPocket(creditorId)
        val debtor = newWaterPocket(debtorId, "100.00")
        whenever(waterPocketRepository.findById(creditorId)).thenReturn(
            Optional.of(creditor)
        )
        whenever(waterPocketRepository.findById(debtorId)).thenReturn(
            Optional.of(debtor)
        )

        val exception = assertThrows<InsufficientQuantityException> {
            waterPocketService.settle(debtorId, creditorId, "200".toBigDecimal())
        }
        assertEquals("Insufficient storage on water pocket", exception.reason)
    }

    @Test
    fun `when payment quantity is bigger than debt should throw Insufficient Quantity Exception`() {
        val creditorId = 123
        val debtorId = 456
        val creditor = newWaterPocket(creditorId)
        val debtor = newWaterPocket(debtorId, "300.00")
        val loanId = LoanEntityId(creditor = creditor, debtor = debtor)
        val loan = LoanEntity(loanId, "150.00".toBigDecimal())
        whenever(waterPocketRepository.findById(creditorId)).thenReturn(
            Optional.of(creditor)
        )
        whenever(waterPocketRepository.findById(debtorId)).thenReturn(
            Optional.of(debtor)
        )
        whenever(loanRepository.findById(loanId)).thenReturn(
            Optional.of(loan)
        )

        val exception = assertThrows<InsufficientQuantityException> {
            waterPocketService.settle(debtorId, creditorId, "200.00".toBigDecimal())
        }
        assertEquals("Payment is bigger than debt", exception.reason)
    }

    @Test
    fun `when payment and no loan found should throw Insufficient Quantity Exception`() {
        val creditorId = 123
        val debtorId = 456
        val creditor = newWaterPocket(creditorId)
        val debtor = newWaterPocket(debtorId, "300.00")
        whenever(waterPocketRepository.findById(creditorId)).thenReturn(
            Optional.of(creditor)
        )
        whenever(waterPocketRepository.findById(debtorId)).thenReturn(
            Optional.of(debtor)
        )

        val exception = assertThrows<InsufficientQuantityException> {
            waterPocketService.settle(debtorId, creditorId, "200.00".toBigDecimal())
        }
        assertEquals("Payment is bigger than debt", exception.reason)
    }

    @Test
    fun `when valid payment should return updated storage and save loan and water pocket storages`() {
        val creditorId = 123
        val debtorId = 456
        val creditor = newWaterPocket(creditorId, "0.00")
        val debtor = newWaterPocket(debtorId, "100.00")
        whenever(waterPocketRepository.findById(creditorId)).thenReturn(
            Optional.of(creditor)
        )
        whenever(waterPocketRepository.findById(debtorId)).thenReturn(
            Optional.of(debtor)
        )
        val loanId = LoanEntityId(creditor = creditor, debtor = debtor)
        val loan = LoanEntity(loanId, "150.00".toBigDecimal())
        whenever(loanRepository.findById(loanId))
            .thenReturn(Optional.of(loan))

        // act
        val debtorResult = waterPocketService.settle(debtorId, creditorId, "100".toBigDecimal())

        // verify result
        assertEquals(0.0, debtorResult.storage.toDouble())
        assertEquals(debtorId, debtorResult.id)

        // verify updated loan value
        val loanCaptor = argumentCaptor<LoanEntity>()
        verify(loanRepository).save(loanCaptor.capture())
        assertEquals(50.0, loanCaptor.firstValue.quantity.toDouble())

        // verify saved updated storage values
        val wpCaptor = argumentCaptor<WaterPocketEntity>()
        verify(waterPocketRepository, times(2)).save(wpCaptor.capture())
        val savedCreditor = wpCaptor.allValues.find { it.id == creditorId }!!
        val savedDebtor = wpCaptor.allValues.find { it.id == debtorId }!!
        assertEquals(100.0, savedCreditor.storage.toDouble())
        assertEquals(0.0, savedDebtor.storage.toDouble())
    }

    @Test
    fun `when full payment should remove loan`() {
        val creditorId = 123
        val debtorId = 456
        val creditor = newWaterPocket(creditorId, "0.00")
        val debtor = newWaterPocket(debtorId, "100.00")
        whenever(waterPocketRepository.findById(creditorId)).thenReturn(
            Optional.of(creditor)
        )
        whenever(waterPocketRepository.findById(debtorId)).thenReturn(
            Optional.of(debtor)
        )
        val loanId = LoanEntityId(creditor = creditor, debtor = debtor)
        val loan = LoanEntity(loanId, "100.00".toBigDecimal())
        whenever(loanRepository.findById(loanId))
            .thenReturn(Optional.of(loan))

        // act
        waterPocketService.settle(debtorId = debtorId, creditorId = creditorId, quantity = "100".toBigDecimal())

        // verify remove loan
        val loanCaptor = argumentCaptor<LoanEntity>()
        verify(loanRepository).delete(loanCaptor.capture())
    }

    @Test
    fun `should not allow borrow from itself`() {
        val id = 123

        val exception = assertThrows<SelfOperationException> {
            waterPocketService.loan(id, id, BigDecimal.TEN)
        }
        assertEquals("Cannot make operation to itself", exception.reason)
    }

    @Test
    fun `should not allow payment to itself`() {
        val id = 123

        val exception = assertThrows<SelfOperationException> {
            waterPocketService.settle(id, id, BigDecimal.TEN)
        }

        assertEquals("Cannot make operation to itself", exception.reason)
    }

    @Test
    fun `should assert water pocket exists before finding debts`() {
        val nonExisting = 42

        val exception = assertThrows<WaterPocketNotFoundException> {
            waterPocketService.findAllDebts(nonExisting)
        }

        assertEquals("Water Pocket '42' not found", exception.reason)
    }

    @Test
    fun `should assert water pocket exists before finding operations`() {
        val nonExisting = 42

        val exception = assertThrows<WaterPocketNotFoundException> {
            waterPocketService.findEvents(nonExisting)
        }

        assertEquals("Water Pocket '42' not found", exception.reason)
    }

    private fun newWaterPocket(
        id: Int,
        storage: String = "10"
    ) = WaterPocketEntity(name = "water-pocket", storage = storage.toBigDecimal(), id = id)

}
