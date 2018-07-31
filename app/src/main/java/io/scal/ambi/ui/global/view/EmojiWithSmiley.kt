package io.scal.ambi.ui.global.view

/**
 * Created by chandra on 30-07-2018.
 */
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.ambi.work.R
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiPopup

/**
 * Created by chandra on 30-07-2018.
 */

class EmojiWithSmiley : RelativeLayout {
    internal var context: Context
    internal lateinit var rootView: View
    internal lateinit var emojiPopup: EmojiPopup
    internal lateinit var et_data: EmojiEditText
    internal lateinit var ic_smile: ImageView

    constructor(context: Context) : super(context) {
        this.context = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.context = context
        init()
    }

    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rootView = inflater.inflate(R.layout.edittext_simley, this)
        et_data = rootView.findViewById(R.id.et_data)
        ic_smile = rootView.findViewById(R.id.ic_smile)
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiBackspaceClickListener { }
                .setOnEmojiClickListener { emoji, imageView -> }
                .setOnEmojiPopupShownListener { ic_smile.setImageResource(R.drawable.ic_action_smile) }
                .setOnSoftKeyboardOpenListener { }
                .setOnEmojiPopupDismissListener { ic_smile.setImageResource(R.drawable.ic_action_smile) }
                .setOnSoftKeyboardCloseListener { }
                .build(et_data)

        ic_smile.setOnClickListener { emojiPopup.toggle() }
    }

}
