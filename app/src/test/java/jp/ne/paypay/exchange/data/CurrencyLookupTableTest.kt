package jp.ne.paypay.exchange.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@Suppress("SpellCheckingInspection")
class CurrencyLookupTableTest {
    @Test
    fun verifyPopulateTable() {
        assertTrue(CurrencyLookupTable.exchangeRates.isEmpty())
        CurrencyLookupTable.populateTable(
            mapOf(
                "USDEUR" to 0.843502,
                "USDGBP" to 0.75785,
                "USDJPY" to 105.811499,
                "USDUSD" to 1.0
            )
        )
        assertEquals(16, CurrencyLookupTable.exchangeRates.size)
        assertEquals(0.843502, CurrencyLookupTable.exchangeRates["USDEUR"])
        assertEquals(0.75785, CurrencyLookupTable.exchangeRates["USDGBP"])
        assertEquals(105.811499, CurrencyLookupTable.exchangeRates["USDJPY"])
        assertEquals(1.0, CurrencyLookupTable.exchangeRates["USDUSD"])
        assertEquals(1.185533644, CurrencyLookupTable.exchangeRates["EURUSD"])
        assertEquals(0.898456672, CurrencyLookupTable.exchangeRates["EURGBP"])
        assertEquals(125.443092014, CurrencyLookupTable.exchangeRates["EURJPY"])
        assertEquals(1.0, CurrencyLookupTable.exchangeRates["EUREUR"])
        assertEquals(1.319522333, CurrencyLookupTable.exchangeRates["GBPUSD"])
        assertEquals(1.113019727, CurrencyLookupTable.exchangeRates["GBPEUR"])
        assertEquals(139.62063601, CurrencyLookupTable.exchangeRates["GBPJPY"])
        assertEquals(1.0, CurrencyLookupTable.exchangeRates["GBPGBP"])
        assertEquals(0.009450769, CurrencyLookupTable.exchangeRates["JPYUSD"])
        assertEquals(0.007971742, CurrencyLookupTable.exchangeRates["JPYEUR"])
        assertEquals(0.007162265, CurrencyLookupTable.exchangeRates["JPYGBP"])
        assertEquals(1.0, CurrencyLookupTable.exchangeRates["JPYJPY"])
    }
}