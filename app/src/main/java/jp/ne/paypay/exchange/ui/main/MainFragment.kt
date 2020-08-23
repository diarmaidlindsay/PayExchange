package jp.ne.paypay.exchange.ui.main

import android.content.Context
import android.graphics.drawable.Drawable
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
import jp.ne.paypay.exchange.data.CurrencyLookupTable
import jp.ne.paypay.exchange.data.handler.CurrencyHandler
import jp.ne.paypay.exchange.data.model.CurrencyListItem
import jp.ne.paypay.exchange.data.model.CurrencyRateGridItem
import jp.ne.paypay.exchange.ui.adapter.CurrencyRateGridAdapter
import jp.ne.paypay.exchange.ui.adapter.CurrencySpinnerAdapter
import jp.ne.paypay.exchange.utils.SharedPreferencesHelper
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        activity?.let { context ->
            currency_list.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        SharedPreferencesHelper.LAST_SELECTED_CURRENCY_IDX.putAsInt(context, position)
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
        activity?.let { context ->
            currencyHandler.retrieveCurrencyList(context, coroutineContext, viewModel.currencyList)
            viewModel.currencyList.observe(context, { currencyList ->
                val spinnerItems = currencyList.toSortedMap()
                    .map { CurrencyListItem(it.key, it.value, getFlag(it.key, context)) }
                val spinnerAdapter = CurrencySpinnerAdapter(context, spinnerItems)
                currency_list.adapter = spinnerAdapter
                currency_list.setSelection(
                    SharedPreferencesHelper.LAST_SELECTED_CURRENCY_IDX.getAsInt(
                        context,
                        0
                    )
                )
                currencyHandler.retrieveLatestRates(context, coroutineContext, viewModel.currencyRates)
                viewModel.currencyRates.observe(context, { currencyRates ->
                    CurrencyLookupTable.populateTable(currencyRates)
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
        }
    }

    private fun updateCurrencyGrid(context: Context, baseCurrency: String) {
        val currentPosition = currency_quotes.firstVisiblePosition
        val currencyRateGridItems = generateCurrencyRateGridItems(baseCurrency, CurrencyLookupTable.exchangeRates, context)
        val gridAdapter =
            CurrencyRateGridAdapter(context, currencyRateGridItems)
        currency_quotes.adapter = gridAdapter
        currency_quotes.setSelection(currentPosition)
    }

    private fun generateCurrencyRateGridItems(baseCurrency: String, currencyRates: Map<String, Double>, context: Context): List<CurrencyRateGridItem> =
        //find quotes which match the base currency then take the RHS of the currency combination, ie, JPYUSD -> USD
        currencyRates.filter { it.key.take(3) == baseCurrency }
            .toSortedMap()
            .map {
                CurrencyRateGridItem(
                    it.key.takeLast(3),
                    getFlag(it.key.takeLast(3), context),
                    String.format("%.9f", it.value.times(viewModel.currencyAmount.value ?: 1.0))
                )
            }

    private fun getFlag(currencyCode: String, context: Context): Drawable? {
        return if (context.assets.list("flags")
                ?.contains("${currencyCode.toLowerCase(Locale.ROOT)}.png") == true
        ) {
            Drawable.createFromStream(
                context.assets.open("flags/${currencyCode.toLowerCase(Locale.ROOT)}.png"),
                null
            )
        } else {
            Drawable.createFromStream(
                context.assets.open("flags/world.png"),
                null
            )
        }
    }
}