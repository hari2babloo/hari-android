package io.scal.ambi.ui.home.chat.details.data

import com.ambi.work.R

enum class UIChatMessageStatus(val chatTitleId: Int) {

    OTHER_USER_MESSAGE(R.string.chat_details_status_other),
    MY_MESSAGE_PENDING(R.string.chat_details_status_pending),
    MY_MESSAGE_SEND(R.string.chat_details_status_send),
    MY_MESSAGE_SEND_FAILED(R.string.chat_details_status_send_failed),
    MY_MESSAGE_DELIVERED(R.string.chat_details_status_delivered),
    MY_MESSAGE_READ(R.string.chat_details_status_read)
}