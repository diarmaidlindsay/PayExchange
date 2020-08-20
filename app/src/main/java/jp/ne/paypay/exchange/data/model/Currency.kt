package jp.ne.paypay.exchange.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Currency(
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "code")
    val code: String
)