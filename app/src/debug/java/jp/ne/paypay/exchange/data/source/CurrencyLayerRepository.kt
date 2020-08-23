package jp.ne.paypay.exchange.data.source

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.ne.paypay.exchange.BuildConfig
import jp.ne.paypay.exchange.data.client.CurrencyLayerRetrofit
import jp.ne.paypay.exchange.data.service.CurrencyLayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CurrencyLayerRepository(override val coroutineContext: CoroutineContext, context: Context) :
    CurrencyLayerDataSource, CoroutineScope {
    override fun getCurrencyList(callback: CurrencyLayerDataSource.GetCurrencyListCallback) {
        launch {
            CurrencyLayerRetrofit.retrofit.create(CurrencyLayerService::class.java)
                .getCurrencyList(BuildConfig.CURRENCY_LAYER_API_ACCESS_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    callback.onCurrencyListLoaded(response)
                }, { exception ->
                    callback.onCurrencyListLoadFailed(exception.message)
                })
        }
    }

    override fun getLatestCurrencyRates(callback: CurrencyLayerDataSource.GetCurrencyRatesCallback) {
        launch {
            CurrencyLayerRetrofit.retrofit.create(CurrencyLayerService::class.java)
                .getCurrencyRates(BuildConfig.CURRENCY_LAYER_API_ACCESS_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    callback.onCurrencyRatesLoadedSuccess(response)
                }, { exception ->
                    callback.onCurrencyRatesLoadedFail(exception.message)
                })
        }
    }
}