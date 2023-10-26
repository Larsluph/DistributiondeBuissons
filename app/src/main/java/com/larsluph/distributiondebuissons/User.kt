package com.larsluph.distributiondebuissons

import org.json.JSONObject

data class User(
    val name: String,
    val pseudo: String
) {
    constructor(payload: JSONObject): this(payload.getString("name"), payload.getString("pseudo"))
}
