package io.scal.ambi.ui.home.chat.details

import android.databinding.BindingAdapter
import android.util.TypedValue
import android.widget.TextView
import com.vanniktech.emoji.EmojiTextView
import com.vanniktech.emoji.EmojiUtils
import io.scal.ambi.ui.home.chat.details.data.UIChatMessageStatus

object ChatDetailsBinder {

    @JvmStatic
    @BindingAdapter("chatDetailsState")
    fun bindChatDetailsMessageState(textView: TextView, state: UIChatMessageStatus?) {
        if (null == state) {
            textView.text = null
        } else {
            textView.setText(state.chatTitleId)
            if (state == UIChatMessageStatus.MY_MESSAGE_PENDING) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            } else {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("chatDetailsMessage")
    fun bindChatDetailsMessageState(textView: EmojiTextView, message: String?) {
        val fontMetrics = textView.paint.fontMetrics
        val defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent
        if (!message.isNullOrBlank() && EmojiUtils.isOnlyEmojis(message)) {
            textView.setEmojiSize((2.6 * defaultEmojiSize).toInt())
        } else {
            textView.setEmojiSize(defaultEmojiSize.toInt())
        }
        textView.text = message

        textView.requestLayout()
    }
}