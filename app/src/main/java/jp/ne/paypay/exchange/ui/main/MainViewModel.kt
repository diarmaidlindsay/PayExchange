package jp.ne.paypay.exchange.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var currencyAmount: Double = 1.0
    val currencyList: MutableLiveData<Map<String, String>> by lazy {
        MutableLiveData<Map<String, String>>()
    }
    val currencyQuotes: MutableLiveData<Map<String, Double>> by lazy {
        MutableLiveData<Map<String, Double>>()
    }
}