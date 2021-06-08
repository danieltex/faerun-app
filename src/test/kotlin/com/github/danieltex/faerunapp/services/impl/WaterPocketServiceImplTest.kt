package com.github.danieltex.faerunapp.services.impl

import com.github.danieltex.faerunapp.dtos.LoanRequestDTO
import com.github.danieltex.faerunapp.dtos.PaymentRequestDTO
import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.LoanEntityId
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import com.github.danieltex.faerunapp.exceptions.InsufficientQuantityException
import com.github.danieltex.faerunapp.exceptions.SelfOperationException
import com.github.danieltex.faerunapp.exceptions.WaterPocketNotFoundException
import com.github.danieltex.faerunapp.repositories.LoanRepository
import com.github.danieltex.faerunapp.repositories.WaterPocketRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
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
    fun `when loan request FROM or TO water-pockets doesn't exist should throw Not Found exception`() {
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
            waterPocketService.loan(debtor, loanRequestDTO(creditor, "200"))
        }
        assertEquals("Insufficient storage on water pocket", exception.message)
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
        val debtorResult = waterPocketService.loan(debtorId, loanRequestDTO(creditorId, "100"))

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
            waterPocketService.settle(existingId, paymentRequestDto(nonExistingId456))
        }
        val fromException = assertThrows<WaterPocketNotFoundException> {
            waterPocketService.settle(nonExistingId789, paymentRequestDto(existingId))
        }

        assertEquals("Water Pocket '456' not found", toException.message)
        assertEquals("Water Pocket '789' not found", fromException.message)
    }

    @Test
    fun `when payment quantity is bigger than storage should throw Insufficient Quantity Exception`() {
        val receiverId = 123
        val payerId = 456
        val receiver = newWaterPocket(receiverId)
        val payer = newWaterPocket(payerId, "100.00")
        whenever(waterPocketRepository.findById(receiverId)).thenReturn(
            Optional.of(receiver)
        )
        whenever(waterPocketRepository.findById(payerId)).thenReturn(
            Optional.of(payer)
        )

        val exception = assertThrows<InsufficientQuantityException> {
            waterPocketService.settle(payerId, paymentRequestDto(receiverId, "200"))
        }
        assertEquals("Insufficient storage on water pocket", exception.message)
    }

    @Test
    fun `when payment quantity is bigger than debt should throw Insufficient Quantity Exception`() {
        val receiverId = 123
        val payerId = 456
        val receiver = newWaterPocket(receiverId)
        val payer = newWaterPocket(payerId, "300.00")
        val loanId = LoanEntityId(payer, receiver)
        val loan = LoanEntity(loanId, "150.00".toBigDecimal())
        whenever(waterPocketRepository.findById(receiverId)).thenReturn(
            Optional.of(receiver)
        )
        whenever(waterPocketRepository.findById(payerId)).thenReturn(
            Optional.of(payer)
        )
        whenever(loanRepository.findById(loanId)).thenReturn(
            Optional.of(loan)
        )

        val exception = assertThrows<InsufficientQuantityException> {
            waterPocketService.settle(payerId, paymentRequestDto(receiverId, "200.00"))
        }
        assertEquals("Payment is bigger than debt", exception.message)
    }

    @Test
    fun `when payment and no loan found should throw Insufficient Quantity Exception`() {
        val receiverId = 123
        val payerId = 456
        val receiver = newWaterPocket(receiverId)
        val payer = newWaterPocket(payerId, "300.00")
        whenever(waterPocketRepository.findById(receiverId)).thenReturn(
            Optional.of(receiver)
        )
        whenever(waterPocketRepository.findById(payerId)).thenReturn(
            Optional.of(payer)
        )

        val exception = assertThrows<InsufficientQuantityException> {
            waterPocketService.settle(payerId, paymentRequestDto(receiverId, "200.00"))
        }
        assertEquals("Payment is bigger than debt", exception.message)
    }

    @Test
    fun `when valid payment should return updated storage and save loan and water pocket storages`() {
        val receiverId = 123
        val payerId = 456
        val receiver = newWaterPocket(receiverId, "0.00")
        val payer = newWaterPocket(payerId, "100.00")
        whenever(waterPocketRepository.findById(receiverId)).thenReturn(
            Optional.of(receiver)
        )
        whenever(waterPocketRepository.findById(payerId)).thenReturn(
            Optional.of(payer)
        )
        val loanId = LoanEntityId(payer, receiver)
        val loan = LoanEntity(loanId, "150.00".toBigDecimal())
        whenever(loanRepository.findById(loanId))
            .thenReturn(Optional.of(loan))

        // act
        val payerResult = waterPocketService.settle(payerId, paymentRequestDto(receiverId, "100"))

        // verify result
        assertEquals(0.0, payerResult.storage.toDouble())
        assertEquals(payerId, payerResult.id)

        // verify updated loan value
        val loanCaptor = argumentCaptor<LoanEntity>()
        verify(loanRepository).save(loanCaptor.capture())
        assertEquals(50.0, loanCaptor.firstValue.quantity.toDouble())

        // verify saved updated storage values
        val wpCaptor = argumentCaptor<WaterPocketEntity>()
        verify(waterPocketRepository, times(2)).save(wpCaptor.capture())
        val savedReceiver = wpCaptor.allValues.find { it.id == receiverId }!!
        val savedPayer = wpCaptor.allValues.find { it.id == payerId }!!
        assertEquals(100.0, savedReceiver.storage.toDouble())
        assertEquals(0.0, savedPayer.storage.toDouble())
    }

    @Test
    fun `when full payment should return remove loan`() {
        val receiverId = 123
        val payerId = 456
        val receiver = newWaterPocket(receiverId, "0.00")
        val payer = newWaterPocket(payerId, "100.00")
        whenever(waterPocketRepository.findById(receiverId)).thenReturn(
            Optional.of(receiver)
        )
        whenever(waterPocketRepository.findById(payerId)).thenReturn(
            Optional.of(payer)
        )
        val loanId = LoanEntityId(payer, receiver)
        val loan = LoanEntity(loanId, "100.00".toBigDecimal())
        whenever(loanRepository.findById(loanId))
            .thenReturn(Optional.of(loan))

        // act
        waterPocketService.settle(payerId, paymentRequestDto(receiverId, "100"))

        // verify remove loan
        val loanCaptor = argumentCaptor<LoanEntity>()
        verify(loanRepository).delete(loanCaptor.capture())
    }

    @Test
    fun `should not allow borrow from itself`() {
        val id = 123

        val exception = assertThrows<SelfOperationException> {
            waterPocketService.loan(id, loanRequestDTO(id))
        }
        assertEquals("Cannot make operation to itself", exception.message)
    }

    @Test
    fun `should not allow payment to itself`() {
        val id = 123

        val exception = assertThrows<SelfOperationException> {
            waterPocketService.settle(id, paymentRequestDto(id))
        }

        assertEquals("Cannot make operation to itself", exception.message)
    }

    private fun loanRequestDTO(
        fromId: Int,
        quantity: String = "10"
    ) = LoanRequestDTO(from = fromId, quantity = quantity.toBigDecimal())

    private fun newWaterPocket(
        id: Int,
        storage: String = "10"
    ) = WaterPocketEntity(name = "water-pocket", storage = storage.toBigDecimal(), id = id)

    private fun paymentRequestDto(
        toId: Int,
        quantity: String = "10"
    ) = PaymentRequestDTO(to = toId, quantity = quantity.toBigDecimal())
}