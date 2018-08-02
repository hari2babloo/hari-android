package io.scal.ambi.extensions.view

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.widget.TextView
import com.ambi.work.R
import java.util.*


/**
 * Created by chandra on 19-07-2018.
 */

open class PartlyBoldTextView : TextView {
    private val RESOURCE_NOT_FOUND = 0
    private lateinit var textToBold: String
    private lateinit var otherText: String

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialise(attrs,0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialise(attrs,defStyleAttr)
    }

    fun initialise(attrs: AttributeSet, defStyleAttr: Int){
        val ta = context.theme .obtainStyledAttributes(attrs, R.styleable.BottomBarType, defStyleAttr, 0)
        try
        {
            textToBold = ta.getString(R.styleable.PartlyBoldTextView_textToBold)
            otherText = ta.getString(R.styleable.PartlyBoldTextView_otherText)
        }
        finally
        {
            ta.recycle()
        }


        this.setText(makeSectionOfTextBold(otherText,textToBold))

    }

    private fun makeSectionOfTextBold(text: String, textToBold: String): SpannableStringBuilder {

        val builder = SpannableStringBuilder()

        if (textToBold.length > 0 && textToBold.trim { it <= ' ' } != "") {

            //for counting start/end indexes
            val testText = text.toLowerCase(Locale.US)
            val testTextToBold = textToBold.toLowerCase(Locale.US)
            val startingIndex = testText.indexOf(testTextToBold)
            val endingIndex = startingIndex + testTextToBold.length
            //for counting start/end indexes

            if (startingIndex < 0 || endingIndex < 0) {
                return builder.append(text)
            } else if (startingIndex >= 0 && endingIndex >= 0) {

                builder.append(text)
                builder.setSpan(StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0)
            }
        } else {
            return builder.append(text)
        }

        return builder
    }
}