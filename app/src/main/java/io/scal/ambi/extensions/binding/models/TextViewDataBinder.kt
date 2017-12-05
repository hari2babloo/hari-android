package io.scal.ambi.extensions.binding.models

import android.databinding.BindingAdapter
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.widget.TextView
import io.scal.ambi.R

object TextViewDataBinder {

    private val fontNameTypefaceCache: MutableMap<String, Typeface> = HashMap()
    private val fontNameWithResource = mapOf(
        Pair("nicolasRegular", R.font.nicolas_desle_pantra_regular),
        Pair("nicolasBold", R.font.nicolas_desle_pantra_bold),
        Pair("nicolasLight", R.font.nicolas_desle_pantra_light),
        Pair("nicolasMedium", R.font.nicolas_desle_pantra_medium)
    )

    @JvmStatic
    @BindingAdapter("customFontName")
    fun bindCustomFont(textView: TextView, fontName: String?) {
        fontName?.run {
            fontNameTypefaceCache[fontName]?.run {
                textView.typeface = this
                return
            }

            val fontResource = fontNameWithResource[this]
            fontResource?.run {
                val typeface = ResourcesCompat.getFont(textView.context, this)
                if (null != typeface) {
                    textView.typeface = typeface
                    fontNameTypefaceCache.put(fontName, typeface)
                }
            }
        }
    }
}