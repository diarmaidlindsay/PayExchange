package jp.ne.paypay.exchange.data

object CurrencyLookupTable {
    var exchangeRates: MutableMap<String, Double> = mutableMapOf()

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
}