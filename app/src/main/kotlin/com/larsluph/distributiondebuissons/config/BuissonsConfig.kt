package com.larsluph.distributiondebuissons.config

import com.larsluph.distributiondebuissons.Colors

data class BuissonsConfig(
    val values: Array<Colors>,
    val shifts: Map<Int, Int>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuissonsConfig

        if (!values.contentEquals(other.values)) return false
        return shifts == other.shifts
    }

    override fun hashCode(): Int {
        var result = values.contentHashCode()
        result = 31 * result + shifts.hashCode()
        return result
    }
}
