package jp.ne.paypay.exchange.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyRatesResponse(
    val success: Boolean = true,
    val terms: String = "0",
    val privacy: String = "0",
    val timestamp: Int = 0,
    val source: String = "USD",
    val quotes: Map<String, Double> = mutableMapOf()
)