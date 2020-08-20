package jp.ne.paypay.exchange.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyJsonResponse(
    @field:Json(name = "success")
    val success: Boolean = true,
    @field:Json(name = "terms")
    val terms: String = "0",
    @field:Json(name = "privacy")
    val privacy: String = "0",
    @field:Json(name = "currencies")
    val currencies: MutableList<Currency> = mutableListOf()
)