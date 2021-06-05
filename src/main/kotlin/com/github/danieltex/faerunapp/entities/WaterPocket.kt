package com.github.danieltex.faerunapp.entities

import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "water_pocket")
class WaterPocket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int?,

    var name: String,

    var storage: BigDecimal
) {
    override fun toString(): String {
        return "WaterPocket(id=$id, name='$name', storage=$storage)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WaterPocket) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}
