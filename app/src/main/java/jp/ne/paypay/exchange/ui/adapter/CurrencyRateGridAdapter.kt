package jp.ne.paypay.exchange.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import jp.ne.paypay.exchange.R
import jp.ne.paypay.exchange.data.model.CurrencyRateGridItem

class CurrencyRateGridAdapter(
    private val context: Context,
    private val currencyRates: List<CurrencyRateGridItem>
) : BaseAdapter() {
    override fun getCount(): Int = currencyRates.size
    override fun getItem(position: Int): Any = currencyRates[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val theView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.currency_rate_grid_item, parent, false)
        val currentItem = getItem(position) as CurrencyRateGridItem
        theView.findViewById<TextView>(R.id.currency_code)?.text = currentItem.currencyCode
        theView.findViewById<ImageView>(R.id.currency_flag)?.setImageDrawable(currentItem.flag)
        theView.findViewById<TextView>(R.id.currency_value)?.text = currentItem.currencyValue

        return theView
    }
}