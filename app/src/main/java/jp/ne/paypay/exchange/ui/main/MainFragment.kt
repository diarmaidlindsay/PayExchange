package jp.ne.paypay.exchange.ui.main

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import jp.ne.paypay.exchange.R
import jp.ne.paypay.exchange.data.CurrencyCalculations
import jp.ne.paypay.exchange.data.CurrencyCalculations.generateCurrencyRateGridItems
import jp.ne.paypay.exchange.data.handler.CurrencyHandler
import jp.ne.paypay.exchange.data.model.CurrencyListItem
import jp.ne.paypay.exchange.data.source.CurrencyLayerRepository
import jp.ne.paypay.exchange.ui.adapter.CurrencyRateGridAdapter
import jp.ne.paypay.exchange.ui.adapter.CurrencySpinnerAdapter
import jp.ne.paypay.exchange.utils.Constants
import jp.ne.paypay.exchange.utils.helper.AssetsHelper.getFlag
import jp.ne.paypay.exchange.utils.helper.ExchangeSharedPreferences
import jp.ne.paypay.exchange.utils.helper.ISharedPreferencesHelper
import jp.ne.paypay.exchange.utils.helper.SharedPreferencesHelper
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.File
import kotlin.coroutines.CoroutineContext

class MainFragment : CoroutineScope, Fragment() {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    var currencyHandler = CurrencyHandler
    var sharedPreferences: ISharedPreferencesHelper = SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        activity?.let { context ->
            val currencyListFile = File(context.filesDir, Constants.CURRENCY_LIST_FILE)
            val repository = CurrencyLayerRepository(coroutineContext, context)
            currencyHandler.retrieveCurrencyList(context, viewModel.currencyList, currencyListFile, repository)
            viewModel.currencyList.observe(context, { currencyList ->
                val spinnerItems = currencyList.toSortedMap()
                    .map { CurrencyListItem(it.key, it.value, getFlag(it.key, context)) }
                val spinnerAdapter = CurrencySpinnerAdapter(context, spinnerItems)
                currency_list.adapter = spinnerAdapter
                currency_list.setSelection(
                    sharedPreferences.getAsInt(
                        context,
                        0,
                        ExchangeSharedPreferences.LAST_SELECTED_CURRENCY_IDX
                    )
                )
                val latestRatesFile = File(context.filesDir, Constants.LATEST_RATES_FILE)
                currencyHandler.retrieveLatestRates(context, viewModel.currencyRates, latestRatesFile, repository)
                viewModel.currencyRates.observe(context, { currencyRates ->
                    //TODO : Convert to background task
                    CurrencyCalculations.populateTable(currencyRates)
                    updateCurrencyGrid(context, viewModel.currentBaseCurrency)
                })
            })
            currency_amount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val enteredAmount = s?.toString()
                    if (enteredAmount.isNullOrBlank().not()) {
                        viewModel.currencyAmount.value = enteredAmount?.toDouble() ?: 1.0
                    }
                }
            })
            viewModel.currencyAmount.observe(context, {
                updateCurrencyGrid(context, viewModel.currentBaseCurrency)
            })
            currency_list.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        sharedPreferences.putAsInt(context, position, ExchangeSharedPreferences.LAST_SELECTED_CURRENCY_IDX)
                        viewModel.currentBaseCurrency = (parent?.getItemAtPosition(position) as CurrencyListItem).currencyCode
                        updateCurrencyGrid(context, viewModel.currentBaseCurrency)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private fun updateCurrencyGrid(context: Context, baseCurrency: String) {
        val currentPosition = currency_quotes.firstVisiblePosition
        val currencyRateGridItems =
            generateCurrencyRateGridItems(baseCurrency, CurrencyCalculations.exchangeRates, viewModel.currencyAmount.value ?: 1.0, context)
        val gridAdapter =
            CurrencyRateGridAdapter(context, currencyRateGridItems)
        currency_quotes.adapter = gridAdapter
        currency_quotes.setSelection(currentPosition)
    }
}