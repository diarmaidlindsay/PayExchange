package jp.ne.paypay.exchange.data.source

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import jp.ne.paypay.exchange.data.model.CurrencyListResponse
import jp.ne.paypay.exchange.data.model.CurrencyRatesResponse
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class CurrencyLayerRepository(
    override val coroutineContext: CoroutineContext,
    private val context: Context
) :
    CurrencyLayerDataSource, CoroutineScope {
    var moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    override fun getCurrencyList(callback: CurrencyLayerDataSource.GetCurrencyListCallback) {
        val adapter: JsonAdapter<CurrencyListResponse> =
            moshi.adapter(CurrencyListResponse::class.java)

        val rawJson = context.assets.open("currencies.json").bufferedReader().use { it.readText() }
        adapter.fromJson(rawJson)?.let { callback.onCurrencyListLoaded(it) }
    }

    override fun getLatestCurrencyRates(callback: CurrencyLayerDataSource.GetCurrencyRatesCallback) {
        val adapter: JsonAdapter<CurrencyRatesResponse> =
            moshi.adapter(CurrencyRatesResponse::class.java)

        val rawJson = context.assets.open("sample_rates.json").bufferedReader().use { it.readText() }
        adapter.fromJson(rawJson)?.let { callback.onCurrencyRatesLoadedSuccess(it) }
    }
}