package jp.ne.paypay.exchange.data.source

import jp.ne.paypay.exchange.data.model.CurrencyListResponse
import jp.ne.paypay.exchange.data.model.CurrencyRatesResponse

interface CurrencyLayerDataSource {
    interface GetCurrencyListCallback {
        fun onCurrencyListLoaded(currencyJsonResponse: CurrencyListResponse)
        fun onCurrencyListLoadFailed(message: String?)
    }

    interface GetCurrencyRatesCallback {
        fun onCurrencyRatesLoadedSuccess(currencyRatesResponse: CurrencyRatesResponse)
        fun onCurrencyRatesLoadedFail(message: String?)
    }

    fun getCurrencyList(callback: GetCurrencyListCallback)
    fun getLatestCurrencyRates(callback: GetCurrencyRatesCallback)
}