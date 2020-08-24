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
import jp.ne.paypay.exchange.utils.annotation.OpenForTesting
import jp.ne.paypay.exchange.utils.helper.ExchangeSharedPreferences
import jp.ne.paypay.exchange.utils.helper.ISharedPreferencesHelper
import jp.ne.paypay.exchange.utils.helper.SharedPreferencesHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.HashMap

object CurrencyHandler {
    var ratesInterface: RatesInterface = RatesImpl
    var currencyListInterface: CurrencyListInterface = CurrencyListImpl
    var sharedPreferences: ISharedPreferencesHelper = SharedPreferencesHelper

    fun retrieveCurrencyList(context: Context?, viewModelData: MutableLiveData<Map<String, String>>, currencyListFile: File, repository: CurrencyLayerRepository) {
        if (currencyListFile.exists()) {
            val currencyList: MutableMap<String, String> = HashMap()
            val properties = Properties()
            properties.load(FileInputStream(currencyListFile))
            for (key in properties.stringPropertyNames()) {
                currencyList[key] = properties[key].toString()
            }
            viewModelData.value = currencyList
        } else {
            currencyListInterface.downloadCurrencyList(context, currencyListFile, viewModelData, repository)
        }
    }

    fun retrieveLatestRates(context: Context?, viewModelData: MutableLiveData<Map<String, Double>>, latestRatesFile: File, repository: CurrencyLayerRepository) {
        val unixTime = System.currentTimeMillis() / 1000L
        val latestTimestamp = sharedPreferences.getAsString(context, "0", ExchangeSharedPreferences.CURRENT_QUOTES_TIMESTAMP) ?: "0"
        if (latestRatesFile.exists() && unixTime - latestTimestamp.toLong() < Constants.RATES_REFRESH_TIME_SECS) {
            ratesInterface.useStoredRates(latestRatesFile, viewModelData)
        } else {
            ratesInterface.downloadLatestRates(context, viewModelData, latestRatesFile, repository)
        }
    }
}

@OpenForTesting
interface CurrencyListInterface {
    fun downloadCurrencyList(context: Context?, currencyListFile: File, viewModelData: MutableLiveData<Map<String, String>>, repository: CurrencyLayerRepository)
}

@OpenForTesting
interface RatesInterface {
    fun downloadLatestRates(context: Context?, viewModelData: MutableLiveData<Map<String, Double>>, latestRatesFile: File, repository: CurrencyLayerRepository)
    fun useStoredRates(latestRatesFile: File, viewModelData: MutableLiveData<Map<String, Double>>)
}

object RatesImpl : RatesInterface {
    override fun useStoredRates(latestRatesFile: File, viewModelData: MutableLiveData<Map<String, Double>>) {
        //use existing stored rates/quotes
        val properties = Properties()
        properties.load(FileInputStream(latestRatesFile))
        val currentRates: MutableMap<String, Double> = HashMap()
        for (key in properties.stringPropertyNames()) {
            currentRates[key] = properties[key].toString().toDouble()
        }
        viewModelData.value = currentRates
    }

    override fun downloadLatestRates(context: Context?, viewModelData: MutableLiveData<Map<String, Double>>, latestRatesFile: File, repository: CurrencyLayerRepository) {
        repository.getLatestCurrencyRates(object : CurrencyLayerDataSource.GetCurrencyRatesCallback {
            override fun onCurrencyRatesLoadedSuccess(currencyRatesResponse: CurrencyRatesResponse) {
                val unixTime = System.currentTimeMillis() / 1000L
                //                SharedPreferencesHelper.putAsString(context, unixTime.toString(), ExchangeSharedPreferences.CURRENT_QUOTES_TIMESTAMP)
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

object CurrencyListImpl : CurrencyListInterface {
    override fun downloadCurrencyList(context: Context?, currencyListFile: File, viewModelData: MutableLiveData<Map<String, String>>, repository: CurrencyLayerRepository) {
        repository.getCurrencyList(object : CurrencyLayerDataSource.GetCurrencyListCallback {
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