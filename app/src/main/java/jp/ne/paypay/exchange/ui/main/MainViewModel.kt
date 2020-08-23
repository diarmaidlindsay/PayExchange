package jp.ne.paypay.exchange.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val currencyAmount: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>(1.0)
    }

    var currentBaseCurrency: String = "USD"

    val currencyList: MutableLiveData<Map<String, String>> by lazy {
        MutableLiveData<Map<String, String>>()
    }
    val currencyRates: MutableLiveData<Map<String, Double>> by lazy {
        MutableLiveData<Map<String, Double>>()
    }
}