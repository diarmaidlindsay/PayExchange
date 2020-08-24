package jp.ne.paypay.exchange.data

import android.content.Context
import jp.ne.paypay.exchange.data.model.CurrencyRateGridItem
import jp.ne.paypay.exchange.utils.helper.AssetsHelper
import jp.ne.paypay.exchange.utils.helper.IAssetsHelper

object CurrencyCalculations {
    var exchangeRates: MutableMap<String, Double> = mutableMapOf()
    var assetsHelper: IAssetsHelper = AssetsHelper

    fun populateTable(quotes: Map<String, Double>) {
        //Base currencies without "USD" prefix
        val quotesWithShortenedKeys =
            quotes.mapKeys { it.key.drop(3) }
        //Generate list of "Currency -> Currency"
        val baseCurrencyListFromUSD: MutableList<String> = mutableListOf()
        quotesWithShortenedKeys.keys.forEach { keyLeft ->
            quotesWithShortenedKeys.keys.forEach { keyRight ->
                baseCurrencyListFromUSD.add(
                    keyLeft + keyRight
                )
            }
        }
        //Extrapolate rates based on original USD -> XXX value
        baseCurrencyListFromUSD.forEach { currencyCombination ->
            val currency1Value = quotesWithShortenedKeys[currencyCombination.take(3)]
            val currency2Value = quotesWithShortenedKeys[currencyCombination.takeLast(3)]
            if (currency1Value != null && currency2Value != null) {
                // 9 decimal places was chosen because the Excel calculator used this by default when I calculated by hand
                exchangeRates[currencyCombination] =
                    String.format("%.9f", currency2Value.div(currency1Value)).toDouble()
            } else {
                exchangeRates[currencyCombination] = 0.0
            }
        }
    }

    fun generateCurrencyRateGridItems(baseCurrency: String, currencyRates: Map<String, Double>, currencyAmount: Double, context: Context?): List<CurrencyRateGridItem> =
        //find quotes which match the base currency then take the RHS of the currency combination, ie, JPYUSD -> USD
        currencyRates.filter { it.key.take(3) == baseCurrency }
            .toSortedMap()
            .map {
                CurrencyRateGridItem(
                    it.key.takeLast(3),
                    assetsHelper.getFlag(it.key.takeLast(3), context),
                    String.format("%.9f", it.value.times(currencyAmount))
                )
            }
}