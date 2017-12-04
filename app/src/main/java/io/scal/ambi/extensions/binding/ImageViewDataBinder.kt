package io.scal.ambi.extensions.binding

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.facebook.common.util.UriUtil
import com.facebook.drawee.view.SimpleDraweeView

object ImageViewDataBinder {

    @JvmStatic
    @BindingAdapter("srcVector")
    fun setSrcVector(imageView: ImageView, @DrawableRes res: Int) {
        imageView.setImageResource(res)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageDrawable(view: ImageView, drawable: Drawable) {
        view.setImageDrawable(drawable)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setAndroidImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @JvmStatic
    @BindingAdapter("imageUri")
    fun setImageUri(simpleDraweeView: SimpleDraweeView, imageUri: Uri?) {
        simpleDraweeView.setImageURI(imageUri)
    }

    @JvmStatic
    @BindingAdapter("imageResource")
    fun setImageResource(simpleDraweeView: SimpleDraweeView, imageRes: Int) {
        var imageUri: Uri? = null
        if (0 != imageRes) {
            imageUri = Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(imageRes.toString())
                .build()
        }
        simpleDraweeView.setImageURI(imageUri)
    }
}