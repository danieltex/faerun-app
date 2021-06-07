package com.github.danieltex.faerunapp.entities

import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "water_pocket")
class WaterPocketEntity(
    var name: String,
    var storage: BigDecimal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
) {
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
