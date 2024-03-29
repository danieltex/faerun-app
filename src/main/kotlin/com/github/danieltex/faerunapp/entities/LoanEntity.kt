package com.github.danieltex.faerunapp.entities

import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.Digits

@Entity
@Table(name = "loan")
class LoanEntity(
    @EmbeddedId
    var id: LoanEntityId,

    @Digits(integer = 19, fraction = 2)
    @Column(precision = 21, scale = 2)
    var quantity: BigDecimal
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LoanEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

@Embeddable
class LoanEntityId(
    @ManyToOne
    var creditor: WaterPocketEntity,
    @ManyToOne
    var debtor: WaterPocketEntity
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LoanEntityId) return false

        if (creditor != other.creditor) return false
        if (debtor != other.debtor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = creditor.hashCode()
        result = 31 * result + debtor.hashCode()
        return result
    }
}
