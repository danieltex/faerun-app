package com.github.danieltex.faerunapp.dtos

import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import com.github.danieltex.faerunapp.entities.WaterPocketEventEntity
import com.github.danieltex.faerunapp.services.balance.Operation

fun WaterPocketEntity.toDTO(): WaterPocketDTO = WaterPocketDTO(name = name, storage = storage, id = id)

fun WaterPocketDTO.toEntity(): WaterPocketEntity = WaterPocketEntity(name = name, storage = storage, id = id)

fun Collection<LoanEntity>.toDTO(): DebitListDTO = DebitListDTO(debts = this.map(LoanEntity::toDTO))

fun LoanEntity.toDTO(): DebitDTO = DebitDTO(id = id.creditor.id!!, quantity = quantity)

fun buildBalanceDTO(
    optimizedOperations: List<Operation>,
    waterPocketMap: Map<Int, WaterPocketEntity>
): BalanceDTO {
    val map = mutableMapOf<Int, WaterPocketBalance>()
    optimizedOperations.forEach { operation ->
        // create receive operation
        val creditorBalance = map.computeIfAbsent(operation.creditor) { id ->
            val storage = waterPocketMap.getValue(id).storage
            WaterPocketBalance(storage)
        }
        creditorBalance.operations += operation.toReceiveDTO()

        // create pay operation
        val debtorBalance = map.computeIfAbsent(operation.debtor) { id ->
            val storage = waterPocketMap.getValue(id).storage
            WaterPocketBalance(storage)
        }
        debtorBalance.operations += operation.toPayDTO()
    }
    return BalanceDTO(map)
}

fun buildSettleOperationsDTO(
    waterPocketMap: Map<Int, WaterPocketEntity>,
    optimizedOperations: List<Operation>
): SettleOperationsDTO {
    val storageMap = waterPocketMap.mapValues { (_, value) ->
        value.storage
    }

    val payOperationDetails = optimizedOperations
        .groupBy { it.debtor }
        .mapValues { (_, value) ->
            value.map { it.toPayDTO() }
        }
    return SettleOperationsDTO(payOperationDetails, storageMap)
}

/**
 * Return an OperationDetails as Payment to a Creditor
 */
fun Operation.toPayDTO() = OperationDetails(
    operation = OperationType.PAY,
    destinationId = this.creditor,
    quantity = this.quantity
)

/**
 * Return an OperationDetails as Receipt from a Debtor
 */
fun Operation.toReceiveDTO() = OperationDetails(
    operation = OperationType.RECEIVE,
    destinationId = this.debtor,
    quantity = this.quantity
)

fun WaterPocketEventEntity.toDTO() = EventDTO(
    event = EventTypeDTO.of(eventType),
    date = date,
    target = if (target.id != origin.id) target.id else null,
    quantity = quantity
)
