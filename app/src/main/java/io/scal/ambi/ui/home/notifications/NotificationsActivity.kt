package io.scal.ambi.ui.home.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ambi.work.R
import com.ambi.work.databinding.ActivityNotificationsBinding
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import kotlin.reflect.KClass

/**
 * Created by chandra on 26-07-2018.
 */

class NotificationsActivity : BaseToolbarActivity<NotificationsViewModel, ActivityNotificationsBinding>() {


    override val layoutId: Int = R.layout.activity_notifications
    override val viewModelClass: KClass<NotificationsViewModel> = NotificationsViewModel::class

    private var defaultToolbarType: ToolbarType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar();
    }

    private fun initToolbar() {
        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_close),
                Runnable { router.exit() },
                ToolbarType.TitleContent(getString(R.string.title_notifications)),
                null,
                null,
                null,
                null)

        defaultToolbarType!!.makeScrolling()

        setToolbarType(defaultToolbarType)
    }

    companion object {
        internal val EXTRA_PROFILE_UID = "EXTRA_PROFILE_UID"

        fun createScreen(context: Context) =
                Intent(context, NotificationsActivity::class.java)

    }

}