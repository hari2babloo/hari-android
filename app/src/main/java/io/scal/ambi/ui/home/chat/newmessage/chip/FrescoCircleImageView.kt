package io.scal.ambi.ui.home.chat.newmessage.chip

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.view.SimpleDraweeView
import de.hdodenhof.circleimageview.CircleImageView
import com.ambi.work.R

class FrescoCircleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : CircleImageView(context, attrs, defStyle) {

    private var logicVisibility: Int = View.VISIBLE
    private var lastDataToShow: Uri? = null

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initializeData(null)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeData(null)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeData(null)
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(null)
        initializeData(uri)
    }

    override fun setVisibility(visibility: Int) {
        logicVisibility = visibility
        initializeData(lastDataToShow)
    }

    private fun initializeData(frescoDataToShow: Uri?) {
        if (null == parent) {
            return
        }

        lastDataToShow = frescoDataToShow
        val visibilityInfo = logicVisibility
        val frescoImageView = (parent as ViewGroup).findViewById<SimpleDraweeView>(R.id.avatar_fresco)
        if (null == frescoDataToShow) {
            frescoImageView.setImageURI(null as? String?)
            frescoImageView.visibility = View.GONE
            super.setVisibility(visibilityInfo)
        } else {
            frescoImageView.setImageURI(frescoDataToShow)
            frescoImageView.visibility = visibilityInfo
            super.setVisibility(View.GONE)
        }
    }
}