package io.scal.ambi.ui.home.chat.details

import android.content.Context
import android.content.Intent
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityChatDetailsBinding
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import kotlin.reflect.KClass

class ChatDetailsActivity : BaseToolbarActivity<ChatDetailsViewModel, ActivityChatDetailsBinding>() {

    override val layoutId: Int = R.layout.activity_chat_details
    override val viewModelClass: KClass<ChatDetailsViewModel> = ChatDetailsViewModel::class

    companion object {

        internal val EXTRA_CHAT_UID = "EXTRA_CHAT_UID"

        fun createScreen(context: Context, chatUid: String): Intent =
            Intent(context, ChatDetailsActivity::class.java)
                .putExtra(EXTRA_CHAT_UID, chatUid)
    }
}