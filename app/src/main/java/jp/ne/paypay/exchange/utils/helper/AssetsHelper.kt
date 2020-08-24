package jp.ne.paypay.exchange.utils.helper

import android.content.Context
import android.graphics.drawable.Drawable
import java.util.*

interface IAssetsHelper {
    fun getFlag(currencyCode: String, context: Context?): Drawable?
}

object AssetsHelper : IAssetsHelper {
    override fun getFlag(currencyCode: String, context: Context?): Drawable? {
        return if (context?.assets?.list("flags")
                ?.contains("${currencyCode.toLowerCase(Locale.ROOT)}.png") == true
        ) {
            Drawable.createFromStream(
                context.assets.open("flags/${currencyCode.toLowerCase(Locale.ROOT)}.png"),
                null
            )
        } else {
            Drawable.createFromStream(
                context?.assets?.open("flags/world.png"),
                null
            )
        }
    }
}