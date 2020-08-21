package jp.ne.paypay.exchange.data.source

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import jp.ne.paypay.exchange.data.model.CurrencyJsonResponse
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class CurrencyLayerRepository(
    override val coroutineContext: CoroutineContext,
    private val context: Context
) :
    CurrencyLayerDataSource, CoroutineScope {
    override fun getCurrencyList(callback: CurrencyLayerDataSource.GetCurrencyListCallback) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<CurrencyJsonResponse> =
            moshi.adapter(CurrencyJsonResponse::class.java)

        val rawJson = context.assets.open("currencies.json").bufferedReader().use { it.readText() }
        adapter.fromJson(rawJson)?.let { callback.onCurrencyListLoaded(it) }
    }
}