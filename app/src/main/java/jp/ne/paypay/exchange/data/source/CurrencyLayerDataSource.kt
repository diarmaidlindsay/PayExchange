package jp.ne.paypay.exchange.data.source

import jp.ne.paypay.exchange.data.model.CurrencyJsonResponse

interface CurrencyLayerDataSource {
    interface GetCurrencyListCallback {
        fun onCurrencyListLoaded(currencyJsonResponse: CurrencyJsonResponse)
        fun onCurrencyListLoadFailed(message: String?)
    }

    fun getCurrencyList(callback: GetCurrencyListCallback)
}