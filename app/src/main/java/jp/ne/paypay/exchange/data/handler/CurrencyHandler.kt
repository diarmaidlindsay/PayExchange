package jp.ne.paypay.exchange.data.handler

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import jp.ne.paypay.exchange.data.model.CurrencyListResponse
import jp.ne.paypay.exchange.data.model.CurrencyRatesResponse
import jp.ne.paypay.exchange.data.source.CurrencyLayerDataSource
import jp.ne.paypay.exchange.data.source.CurrencyLayerRepository
import jp.ne.paypay.exchange.utils.Constants
import jp.ne.paypay.exchange.utils.SharedPreferencesHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

object CurrencyHandler {
    fun retrieveCurrencyList(context: Context, coroutineContext: CoroutineContext, viewModelData: MutableLiveData<Map<String, String>>) {
        val currencyListFile = File(context.filesDir, Constants.CURRENCY_LIST_FILE)
        if (currencyListFile.exists()) {
            val currencyList: MutableMap<String, String> = HashMap()
            val properties = Properties()
            properties.load(FileInputStream(currencyListFile))
            for (key in properties.stringPropertyNames()) {
                currencyList[key] = properties[key].toString()
            }
            viewModelData.value = currencyList
        } else {
            val repo = CurrencyLayerRepository(coroutineContext, context)
            repo.getCurrencyList(object : CurrencyLayerDataSource.GetCurrencyListCallback {
                override fun onCurrencyListLoaded(currencyJsonResponse: CurrencyListResponse) {
                    viewModelData.value = currencyJsonResponse.currencies
                    //store it locally
                    currencyJsonResponse.currencies.toProperties().store(
                        FileOutputStream(currencyListFile, false),
                        null
                    )
                }

                override fun onCurrencyListLoadFailed(message: String?) {
                    Toast.makeText(context, "Currency List Load Failed : $message", Toast.LENGTH_SHORT).show()
                    Log.e("CurrencyHandler", "Currency List Load Failed : $message")
                }
            })
        }
    }

    fun retrieveLatestRates(context: Context, coroutineContext: CoroutineContext, viewModelData: MutableLiveData<Map<String, Double>>) {
        val latestRatesFile = File(context.filesDir, Constants.LATEST_RATES_FILE)
        val unixTime = System.currentTimeMillis() / 1000L
        val latestTimestamp = SharedPreferencesHelper.CURRENT_QUOTES_TIMESTAMP.getAsString(context, "0") ?: "0"
        if (latestRatesFile.exists() && unixTime - latestTimestamp.toLong() < Constants.RATES_REFRESH_TIME_SECS) {
            useStoredRates(latestRatesFile, viewModelData)
        } else {
            downloadLatestRates(context, coroutineContext, viewModelData, latestRatesFile)
        }
    }

    private fun useStoredRates(
        latestRatesFile: File,
        viewModelData: MutableLiveData<Map<String, Double>>
    ) {
        //use existing stored rates/quotes
        val properties = Properties()
        properties.load(FileInputStream(latestRatesFile))
        val currentRates: MutableMap<String, Double> = HashMap()
        for (key in properties.stringPropertyNames()) {
            currentRates[key] = properties[key].toString().toDouble()
        }
        viewModelData.value = currentRates
    }

    private fun downloadLatestRates(context: Context, coroutineContext: CoroutineContext, viewModelData: MutableLiveData<Map<String, Double>>, latestRatesFile: File) {
        val repo = CurrencyLayerRepository(coroutineContext, context)
        repo.getLatestCurrencyRates(object : CurrencyLayerDataSource.GetCurrencyRatesCallback {
            override fun onCurrencyRatesLoadedSuccess(currencyRatesResponse: CurrencyRatesResponse) {
                val unixTime = System.currentTimeMillis() / 1000L
                SharedPreferencesHelper.CURRENT_QUOTES_TIMESTAMP.putAsString(context, unixTime.toString())
                viewModelData.value = currencyRatesResponse.quotes
                //store it locally
                currencyRatesResponse.quotes.mapValues { it.value.toString() }.toProperties().store(
                    FileOutputStream(latestRatesFile, false),
                    null
                )
            }

            override fun onCurrencyRatesLoadedFail(message: String?) {
                Toast.makeText(context, "Failed to get current rates : $message", Toast.LENGTH_SHORT).show()
                Log.e("CurrencyHandler", "Failed to get current rates : $message")
                //Use stored rates as fallback if exist
                if (latestRatesFile.exists()) {
                    useStoredRates(latestRatesFile, viewModelData)
                } else {
                    Log.w("CurrencyHandler", "Could not fallback on stored rates - does not exist")
                }
            }
        })
    }
}