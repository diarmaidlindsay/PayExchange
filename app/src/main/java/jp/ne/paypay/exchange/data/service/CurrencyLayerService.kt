package jp.ne.paypay.exchange.data.service

import io.reactivex.Observable
import jp.ne.paypay.exchange.data.Urls
import jp.ne.paypay.exchange.data.model.CurrencyJsonResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyLayerService {
    @GET(Urls.CURRENCY_LAYER_LIST)
    fun getCurrencyList(@Query(Urls.CURRENCY_LAYER_ACCESS_KEY) accessKey: String): Observable<CurrencyJsonResponse>
}