package jp.ne.paypay.exchange.data.handler

import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import jp.ne.paypay.exchange.data.source.CurrencyLayerRepository
import jp.ne.paypay.exchange.utils.ExchangeSharedPreferences
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.io.File

class CurrencyHandlerTest {
    private lateinit var viewModelData: MutableLiveData<Map<String, Double>>
    private lateinit var currencyLayerRepository: CurrencyLayerRepository

    @Before
    fun setup() {
        viewModelData = MutableLiveData<Map<String, Double>>()
        currencyLayerRepository = mock()
        CurrencyHandler.ratesInterface = mock()
        CurrencyHandler.currencyListInterface = mock()
        CurrencyHandler.sharedPreferences = mock()
    }

    @Test
    fun previouslySavedRatesFileNotExists_verifyRatesDownloaded() {
        val ratesFile: File = mock()
        whenever(ratesFile.exists()).thenReturn(false)

        CurrencyHandler.retrieveLatestRates(null, viewModelData, ratesFile, currencyLayerRepository)

        Mockito.verify(CurrencyHandler.ratesInterface, Mockito.times(0)).useStoredRates(any(), any())
        Mockito.verify(CurrencyHandler.ratesInterface, Mockito.times(1)).downloadLatestRates(null, viewModelData, ratesFile, currencyLayerRepository)
    }

    @Test
    fun previouslySavedRatesFileExists_29minsPassedSinceLastDownload_verifyStoredRatesUsed() {
        val unixTime = System.currentTimeMillis() / 1000L
        val ratesFile: File = mock()
        whenever(ratesFile.exists()).thenReturn(true)
        whenever(CurrencyHandler.sharedPreferences.getAsString(null, "0", ExchangeSharedPreferences.CURRENT_QUOTES_TIMESTAMP)).thenReturn((unixTime - 1740).toString())

        CurrencyHandler.retrieveLatestRates(null, viewModelData, ratesFile, currencyLayerRepository)

        Mockito.verify(CurrencyHandler.ratesInterface, Mockito.times(1)).useStoredRates(any(), any())
        Mockito.verify(CurrencyHandler.ratesInterface, Mockito.times(0)).downloadLatestRates(null, viewModelData, ratesFile, currencyLayerRepository)
    }

    @Test
    fun previouslySavedRatesFileExists_30minsPassedSinceLastDownload_verifyRatesDownloaded() {
        val unixTime = System.currentTimeMillis() / 1000L
        val ratesFile: File = mock()
        whenever(ratesFile.exists()).thenReturn(true)
        whenever(CurrencyHandler.sharedPreferences.getAsString(null, "0", ExchangeSharedPreferences.CURRENT_QUOTES_TIMESTAMP)).thenReturn((unixTime - 1800).toString())

        CurrencyHandler.retrieveLatestRates(null, viewModelData, ratesFile, currencyLayerRepository)

        Mockito.verify(CurrencyHandler.ratesInterface, Mockito.times(0)).useStoredRates(any(), any())
        Mockito.verify(CurrencyHandler.ratesInterface, Mockito.times(1)).downloadLatestRates(null, viewModelData, ratesFile, currencyLayerRepository)
    }
}