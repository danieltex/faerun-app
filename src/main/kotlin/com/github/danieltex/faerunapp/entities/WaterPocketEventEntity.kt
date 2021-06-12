package com.github.danieltex.faerunapp.entities

import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "event")
class WaterPocketEventEntity (
    @ManyToOne
    var origin: WaterPocketEntity,

    val eventType: EventType,

    val date: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    var target: WaterPocketEntity,

    val quantity: BigDecimal? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)

enum class EventType {
    /**
     *
     */
    BORROW_FROM,
    LOAN_TO,
    PAY_TO,
    RECEIVE_FROM,

    /** Origin (debtor) settled his debts to target creditor */
    SETTLE_DEBTS_TO,

    /** Origin (creditor) has his loan to target debtor settled */
    SETTLE_DEBTS_FROM,

    /** Water Pocket created with storage */
    CREATED
}