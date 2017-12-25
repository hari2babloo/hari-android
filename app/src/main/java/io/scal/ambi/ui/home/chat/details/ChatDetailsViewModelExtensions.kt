package io.scal.ambi.ui.home.chat.details

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import io.scal.ambi.R
import io.scal.ambi.entity.chat.*
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.appendCustom
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.ui.home.chat.details.data.*
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.net.URLDecoder
import java.text.DecimalFormat
import java.util.*

internal fun SmallChatItem?.toChatInfo(): UIChatInfo? =
    if (null == this) {
        null
    } else {
        UIChatInfo(icon, title, "")
    }

internal fun FullChatItem.toChatInfo(context: Context): UIChatInfo {
    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    val description =
        when (this) {
            is FullChatItem.Direct -> {
                val description = SpannableStringBuilder()
                description.append(context.getString(R.string.chat_details_info_direct))
                description.append(" ")
                description.appendCustom(otherUser.name, StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                description
            }
            is FullChatItem.Group  -> {
                val dateFormatter = DateTimeFormat.forPattern("MMMM dd, yyyy' at 'HH:mm a").withLocale(Locale.ENGLISH)
                val infoEndMessage = context.getString(R.string.chat_details_info_group_middle, dateFormatter.print(creationDateTime))
                val creatorName = creator.name.let { "\ufeff@$it" }
                val description = SpannableStringBuilder()
                description.appendCustom(creatorName,
                                         ForegroundColorSpan(ContextCompat.getColor(context, R.color.blueHref)),
                                         Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                description.append(" ")
                description.append(infoEndMessage)
                description
            }
            else                   -> throw IllegalArgumentException("unknown type: ${this.javaClass.simpleName}")
        }
    return UIChatInfo(icon, title, description)
}

internal fun ChatMessage.toChatDetailsElement(currentUser: User): List<UIChatMessage> =
    when (this) {
        is ChatMessage.TextMessage       -> listOf(UIChatMessage.TextMessage(uid,
                                                                             sender,
                                                                             myMessageState.toMessageState(),
                                                                             message,
                                                                             sendDate,
                                                                             UIChatLikes(currentUser.uid, likes)))
        is ChatMessage.AttachmentMessage ->
            attachments.map {
                @Suppress("REDUNDANT_ELSE_IN_WHEN")
                when (it) {
                    is ChatAttachment.Image -> UIChatMessage.ImageMessage(uid,
                                                                          sender,
                                                                          myMessageState.toMessageState(),
                                                                          (message + "\n" + it.path.getFileName()).trim(),
                                                                          IconImage(it.path.toString()),
                                                                          sendDate,
                                                                          UIChatLikes(currentUser.uid, likes))
                    is ChatAttachment.File  -> UIChatMessage.AttachmentMessage(uid,
                                                                               sender,
                                                                               myMessageState.toMessageState(),
                                                                               it,
                                                                               ((message + "\n" + it.path.getFileName())).trim(),
                                                                               "${it.size.getFileSize()} ${it.typeName}",
                                                                               sendDate,
                                                                               UIChatLikes(currentUser.uid, likes))
                    else                    -> throw IllegalArgumentException("unknown attachment type")
                }
            }
    }

internal fun ChatMyMessageState?.toMessageState(): UIChatMessageStatus =
    when (this) {
        null                           -> UIChatMessageStatus.OTHER_USER_MESSAGE
        ChatMyMessageState.PENDING     -> UIChatMessageStatus.MY_MESSAGE_PENDING
        ChatMyMessageState.SEND        -> UIChatMessageStatus.MY_MESSAGE_SEND
        ChatMyMessageState.SEND_FAILED -> UIChatMessageStatus.MY_MESSAGE_SEND_FAILED
    }

internal fun List<UIChatMessage>.addDateObjects(): List<Any> {
    // may be we should do it in BG thread.. for now lets do it here
    val result = fold(ArrayList<Any>(),
                      { acc, uiChatMessage ->
                          val lastItem = acc.lastOrNull()
                          if (lastItem is UIChatMessage) {
                              val lastItemMessageLocalDate = lastItem.messageDateTime.toLocalDate()
                              val currentMessageLocalDate = uiChatMessage.messageDateTime.toLocalDate()
                              if (lastItemMessageLocalDate.dayOfYear != currentMessageLocalDate.dayOfYear) {
                                  acc.add(UIChatDate(lastItemMessageLocalDate))
                              }
                          }
                          acc.add(uiChatMessage)
                          acc
                      }
    )
    if (result.isNotEmpty()) {
        val lastItem = result.last()
        if (lastItem is UIChatMessage) {
            result.add(UIChatDate(lastItem.messageDateTime.toLocalDate()))
        }
    }

    return result
}

internal fun Long.getFileSize(): String {
    if (this <= 0)
        return "0"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(toDouble()) / Math.log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(this / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
}

internal fun Uri.getFileName(): String =
    try {
        URLDecoder.decode(File(toString()).name, "utf-8")
    } catch (t: Throwable) {
        ""
    }

internal data class AllMessagesInfo(val serverMessages: List<UIChatMessage>, val chatInfo: UIChatInfo? = null)