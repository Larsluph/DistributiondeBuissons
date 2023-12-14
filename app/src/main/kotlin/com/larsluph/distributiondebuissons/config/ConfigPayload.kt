package com.larsluph.distributiondebuissons.config

data class ConfigPayload(
    val users: Array<User>,
    val buissons: BuissonsConfig
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConfigPayload

        if (!users.contentEquals(other.users)) return false
        return buissons == other.buissons
    }

    override fun hashCode(): Int {
        var result = users.contentHashCode()
        result = 31 * result + buissons.hashCode()
        return result
    }
}
