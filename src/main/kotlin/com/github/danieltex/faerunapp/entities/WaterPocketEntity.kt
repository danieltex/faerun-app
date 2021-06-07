package com.github.danieltex.faerunapp.entities

import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.Digits

@Entity
@Table(name = "water_pocket")
class WaterPocketEntity(
    var name: String,

    @Digits(integer = 19, fraction = 2)
    @Column(precision = 21, scale = 2)
    var storage: BigDecimal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
) : Serializable {
    override fun toString(): String {
        return "WaterPocket(id=$id, name='$name', storage=$storage)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WaterPocketEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}
