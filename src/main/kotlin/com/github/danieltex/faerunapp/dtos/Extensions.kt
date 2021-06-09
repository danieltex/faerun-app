package com.github.danieltex.faerunapp.dtos

import com.github.danieltex.faerunapp.entities.LoanEntity
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
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
        creditorBalance.operations += OperationDetails(
            operation = OperationType.RECEIVE,
            destinationId = operation.debtor,
            quantity = operation.quantity
        )

        // create pay operation
        val debtorBalance = map.computeIfAbsent(operation.debtor) { id ->
            val storage = waterPocketMap.getValue(id).storage
            WaterPocketBalance(storage)
        }
        debtorBalance.operations += OperationDetails(
            operation = OperationType.PAY,
            destinationId = operation.creditor,
            quantity = operation.quantity
        )
    }
    return BalanceDTO(map)
}
