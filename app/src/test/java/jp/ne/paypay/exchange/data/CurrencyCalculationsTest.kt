package jp.ne.paypay.exchange.data

import com.nhaarman.mockitokotlin2.mock
import jp.ne.paypay.exchange.data.model.CurrencyRateGridItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CurrencyCalculationsTest {

    @Before
    fun setup() {
        CurrencyCalculations.assetsHelper = mock()
    }

    @Test
    fun verifyPopulateTable() {
        assertTrue(CurrencyCalculations.exchangeRates.isEmpty())
        CurrencyCalculations.populateTable(
            mapOf(
                "USDEUR" to 0.843502,
                "USDGBP" to 0.75785,
                "USDJPY" to 105.811499,
                "USDUSD" to 1.0
            )
        )
        assertEquals(16, CurrencyCalculations.exchangeRates.size)
        assertEquals(0.843502, CurrencyCalculations.exchangeRates["USDEUR"])
        assertEquals(0.75785, CurrencyCalculations.exchangeRates["USDGBP"])
        assertEquals(105.811499, CurrencyCalculations.exchangeRates["USDJPY"])
        assertEquals(1.0, CurrencyCalculations.exchangeRates["USDUSD"])
        assertEquals(1.185533644, CurrencyCalculations.exchangeRates["EURUSD"])
        assertEquals(0.898456672, CurrencyCalculations.exchangeRates["EURGBP"])
        assertEquals(125.443092014, CurrencyCalculations.exchangeRates["EURJPY"])
        assertEquals(1.0, CurrencyCalculations.exchangeRates["EUREUR"])
        assertEquals(1.319522333, CurrencyCalculations.exchangeRates["GBPUSD"])
        assertEquals(1.113019727, CurrencyCalculations.exchangeRates["GBPEUR"])
        assertEquals(139.62063601, CurrencyCalculations.exchangeRates["GBPJPY"])
        assertEquals(1.0, CurrencyCalculations.exchangeRates["GBPGBP"])
        assertEquals(0.009450769, CurrencyCalculations.exchangeRates["JPYUSD"])
        assertEquals(0.007971742, CurrencyCalculations.exchangeRates["JPYEUR"])
        assertEquals(0.007162265, CurrencyCalculations.exchangeRates["JPYGBP"])
        assertEquals(1.0, CurrencyCalculations.exchangeRates["JPYJPY"])
    }

    @Test
    fun verifyGenerateGridItems() {
        var gridItems = CurrencyCalculations.generateCurrencyRateGridItems(
            "USD",
            mapOf(
                "USDUSD" to 1.0,
                "USDEUR" to 1.1,
                "USDGBP" to 1.2
            ),
            1.0,
            null
        )
        assertEquals(listOf(
            CurrencyRateGridItem(currencyCode = "EUR", flag = null, currencyValue = "1.100000000"),
            CurrencyRateGridItem(currencyCode = "GBP", flag = null, currencyValue = "1.200000000"),
            CurrencyRateGridItem(currencyCode = "USD", flag = null, currencyValue = "1.000000000")
        ), gridItems
        )

        gridItems = CurrencyCalculations.generateCurrencyRateGridItems(
            "USD",
            mapOf(
                "USDUSD" to 1.0,
                "USDEUR" to 1.1,
                "USDGBP" to 1.2
            ),
            0.01,
            null
        )
        assertEquals(listOf(
            CurrencyRateGridItem(currencyCode = "EUR", flag = null, currencyValue = "0.011000000"),
            CurrencyRateGridItem(currencyCode = "GBP", flag = null, currencyValue = "0.012000000"),
            CurrencyRateGridItem(currencyCode = "USD", flag = null, currencyValue = "0.010000000")
        ), gridItems
        )

        gridItems = CurrencyCalculations.generateCurrencyRateGridItems(
            "USD",
            mapOf(
                "USDUSD" to 1.0,
                "USDEUR" to 1.1,
                "USDGBP" to 1.2
            ),
            10000000.0,
            null
        )
        assertEquals(listOf(
            CurrencyRateGridItem(currencyCode = "EUR", flag = null, currencyValue = "11000000.000000000"),
            CurrencyRateGridItem(currencyCode = "GBP", flag = null, currencyValue = "12000000.000000000"),
            CurrencyRateGridItem(currencyCode = "USD", flag = null, currencyValue = "10000000.000000000")
        ), gridItems
        )
    }
}