package jp.ne.paypay.exchange

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.ne.paypay.exchange.data.model.CurrencyJsonResponse
import jp.ne.paypay.exchange.data.source.CurrencyLayerDataSource
import jp.ne.paypay.exchange.data.source.CurrencyLayerRepository
import jp.ne.paypay.exchange.ui.main.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class MainActivity : CoroutineScope, AppCompatActivity() {
    private val job = Job()
    final override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        val repo = CurrencyLayerRepository(coroutineContext)
        repo.getCurrencyList(object : CurrencyLayerDataSource.GetCurrencyListCallback {
            override fun onCurrencyListLoaded(currencyJsonResponse: CurrencyJsonResponse) {
                print(currencyJsonResponse)
            }

            override fun onCurrencyListLoadFailed(message: String?) {
                TODO("Not yet implemented")
            }

        })
    }
}