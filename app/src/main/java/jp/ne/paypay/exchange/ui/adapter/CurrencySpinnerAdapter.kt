package jp.ne.paypay.exchange.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import jp.ne.paypay.exchange.R
import jp.ne.paypay.exchange.data.model.CurrencyListItem

class CurrencySpinnerAdapter(
    private val context: Context,
    private val currencyCodes: List<CurrencyListItem>
) : BaseAdapter() {

    override fun getCount(): Int = currencyCodes.size
    override fun getItem(position: Int): Any = currencyCodes[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val theView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.currency_spinner_item, parent, false)
        val currentItem = getItem(position) as CurrencyListItem
        theView.findViewById<TextView>(R.id.currency_code)?.text = currentItem.currencyCode
        theView.findViewById<ImageView>(R.id.currency_flag)?.setImageDrawable(currentItem.flag)
        theView.findViewById<TextView>(R.id.currency_name)?.text = currentItem.currencyName

        return theView
    }
}