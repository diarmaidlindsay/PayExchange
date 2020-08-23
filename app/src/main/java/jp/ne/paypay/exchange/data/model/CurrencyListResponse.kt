package jp.ne.paypay.exchange.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyListResponse(
    val success: Boolean = true,
    val terms: String = "0",
    val privacy: String = "0",
    val currencies: Map<String, String> = mutableMapOf()
)