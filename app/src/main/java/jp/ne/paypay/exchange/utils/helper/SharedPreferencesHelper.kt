package jp.ne.paypay.exchange.utils.helper

import android.content.Context
import android.content.SharedPreferences

interface ISharedPreferencesHelper {
    fun getSharedPreferences(context: Context?): SharedPreferences?
    fun putAsString(context: Context?, value: String?, pref: ExchangeSharedPreferences)
    fun getAsString(context: Context?, defaultValue: String?, pref: ExchangeSharedPreferences): String?
    fun putAsInt(context: Context?, value: Int?, pref: ExchangeSharedPreferences)
    fun getAsInt(context: Context?, defaultValue: Int, pref: ExchangeSharedPreferences): Int
}

object SharedPreferencesHelper : ISharedPreferencesHelper {
    override fun getSharedPreferences(context: Context?): SharedPreferences? =
        context?.getSharedPreferences("exchange", Context.MODE_PRIVATE)


    override fun putAsString(context: Context?, value: String?, pref: ExchangeSharedPreferences) {
        val editor = getSharedPreferences(context)?.edit()
        editor?.putString(pref.key, value)
        editor?.apply()
    }

    override fun getAsString(context: Context?, defaultValue: String?, pref: ExchangeSharedPreferences): String? =
        getSharedPreferences(context)?.getString(pref.key, defaultValue) ?: defaultValue

    override fun putAsInt(context: Context?, value: Int?, pref: ExchangeSharedPreferences) {
        val editor = getSharedPreferences(context)?.edit()
        editor?.putInt(pref.key, value ?: 0)
        editor?.apply()
    }

    override fun getAsInt(context: Context?, defaultValue: Int, pref: ExchangeSharedPreferences): Int =
        getSharedPreferences(context)?.getInt(pref.key, defaultValue) ?: defaultValue
}

enum class ExchangeSharedPreferences constructor(val key: String?) {
    CURRENT_QUOTES_TIMESTAMP("current_quotes_timestamp"),
    LAST_SELECTED_CURRENCY_IDX("last_selected_currency");
}