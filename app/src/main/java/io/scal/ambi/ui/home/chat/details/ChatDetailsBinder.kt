package io.scal.ambi.ui.home.chat.details

import android.content.Context
import android.databinding.BindingAdapter
import android.util.TypedValue
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.vanniktech.emoji.EmojiTextView
import com.vanniktech.emoji.EmojiUtils
import com.ambi.work.R
import io.scal.ambi.extensions.binding.binders.ImageViewDataBinder
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImage
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

    @JvmStatic
    @BindingAdapter("chatDetailsAttachmentTypeIcon")
    fun bindChatDetailsAttachmentTypeIcon(imageView: SimpleDraweeView, typeName: String?) {
        if (null == typeName) {
            imageView.setImageURI(null as String?)
        } else {
            val imagePath = typeName.typeIcon(imageView.context)
            ImageViewDataBinder.setImageString(imageView, IconImage(imagePath))
        }
    }

    private fun String.typeIcon(context: Context): String {
        val lowerCase = toLowerCase()
        val drawableId = context.resources.getIdentifier("ic_extension_$lowerCase", "drawable", context.packageName)
        return (if (0 == drawableId) R.drawable.ic_extension_unknown else drawableId).toFrescoImagePath()
    }
}