package jp.ne.paypay.exchange.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import jp.ne.paypay.exchange.R
import jp.ne.paypay.exchange.data.model.CurrencyJsonResponse
import jp.ne.paypay.exchange.data.source.CurrencyLayerDataSource
import jp.ne.paypay.exchange.data.source.CurrencyLayerRepository
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


class MainFragment : CoroutineScope, Fragment() {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        activity?.let { context ->
            val repo = CurrencyLayerRepository(coroutineContext, context)
            repo.getCurrencyList(object : CurrencyLayerDataSource.GetCurrencyListCallback {
                override fun onCurrencyListLoaded(currencyJsonResponse: CurrencyJsonResponse) {
                    print(currencyJsonResponse)
                    viewModel.currencyList.value = currencyJsonResponse.currencies
                }

                override fun onCurrencyListLoadFailed(message: String?) {
                    TODO("Not yet implemented")
                }
            })
            viewModel.currencyList.observe(context, Observer { currencyList ->
                val adapter: ArrayAdapter<String> =
                    ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_spinner_item,
                        currencyList.keys.toTypedArray()
                    )
                currency_list.adapter = adapter
            })
        }
    }

}