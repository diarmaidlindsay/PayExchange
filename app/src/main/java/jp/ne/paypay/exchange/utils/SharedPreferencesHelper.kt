package jp.ne.paypay.exchange.utils

import android.content.Context
import android.content.SharedPreferences

enum class SharedPreferencesHelper constructor(val key: String?) {
    CURRENT_QUOTES_TIMESTAMP("current_quotes_timestamp"),
    LAST_SELECTED_CURRENCY_IDX("last_selected_currency");

    private fun getSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("exchange", Context.MODE_PRIVATE)

    fun putAsString(context: Context, value: String?) {
        val editor = getSharedPreferences(context).edit()
        editor?.putString(this.key, value)
        editor?.apply()
    }

    fun getAsString(context: Context, defaultValue: String?): String? = getSharedPreferences(context).getString(this.key, defaultValue)

    fun putAsInt(context: Context, value: Int?) {
        val editor = getSharedPreferences(context).edit()
        editor?.putInt(this.key, value ?: 0)
        editor?.apply()
    }

    fun getAsInt(context: Context, defaultValue: Int): Int = getSharedPreferences(context).getInt(this.key, defaultValue)
}