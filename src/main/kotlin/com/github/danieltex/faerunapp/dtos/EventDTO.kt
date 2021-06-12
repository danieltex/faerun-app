package com.github.danieltex.faerunapp.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.danieltex.faerunapp.entities.EventType
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class EventDTO(
    val event: EventTypeDTO,

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "dd-MM-yyyy HH:mm:ss"
    )
    val date: LocalDateTime,
    var target: Int?,
    val quantity: BigDecimal? = null,
)

enum class EventTypeDTO(val type: EventType) {
    BORROW_FROM(EventType.BORROW_FROM),
    LOAN_TO(EventType.LOAN_TO),
    PAY_TO(EventType.PAY_TO),
    RECEIVE_FROM(EventType.RECEIVE_FROM),

    /** Origin (debtor) settled his debts to target creditor */
    SETTLE_DEBTS_TO(EventType.SETTLE_DEBTS_TO),

    /** Origin (creditor) has his loan to target debtor settled */
    SETTLE_DEBTS_FROM(EventType.SETTLE_DEBTS_FROM),

    /** Water Pocket created with storage */
    CREATED(EventType.CREATED);

    companion object {
        fun of(eventType: EventType): EventTypeDTO {
            return EventTypeDTO.values().find {
                it.type == eventType
            } ?: throw IllegalArgumentException("Unknown type $eventType")
        }
    }
}
