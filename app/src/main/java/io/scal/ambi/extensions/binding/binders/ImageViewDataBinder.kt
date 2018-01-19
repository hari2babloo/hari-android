package io.scal.ambi.extensions.binding.binders

import android.content.Context
import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.v7.content.res.AppCompatResources
import android.widget.ImageView
import com.facebook.common.util.UriUtil
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView
import io.scal.ambi.extensions.view.ColorIconImage
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType


object ImageViewDataBinder {

    @JvmStatic
    @BindingAdapter("srcVector")
    fun setSrcVector(imageView: ImageView, @DrawableRes res: Int) {
        imageView.setImageResource(res)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageDrawable(view: ImageView, drawable: Drawable?) {
        view.setImageDrawable(drawable)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setAndroidImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @JvmStatic
    @BindingAdapter("iconImage")
    fun setImageString(simpleDraweeView: SimpleDraweeView, iconImage: IconImage?) {
        simpleDraweeView.clearColorFilter()
        if (null == iconImage) {
            simpleDraweeView.setImageDrawable(null)
            return
        }
        val iconDrawable = getResourceDrawable(simpleDraweeView.context, iconImage.iconPath)
        if (null == iconDrawable) {
            val hierarchy = simpleDraweeView.hierarchy
            hierarchy.setPlaceholderImage(iconImage.placeHolderIconPath?.let {
                getResourceDrawable(simpleDraweeView.context, it)
            })
            simpleDraweeView.hierarchy = hierarchy
            simpleDraweeView.setImageURI(iconImage.iconPath)
        } else {
            simpleDraweeView.setImageDrawable(iconDrawable)
        }
        if (iconImage is ColorIconImage) {
            simpleDraweeView.setColorFilter(iconImage.color)
        }
    }

    @JvmStatic
    @BindingAdapter("imageUri")
    fun setImageUri(simpleDraweeView: SimpleDraweeView, imageUri: Uri?) {
        simpleDraweeView.setImageURI(imageUri)
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUri(simpleDraweeView: SimpleDraweeView, imageUri: String?) {
        if (null != imageUri) {
            val localRespurse = getResourceDrawable(simpleDraweeView.context, imageUri)
            if (null != localRespurse) {
                simpleDraweeView.setImageDrawable(localRespurse)
                return
            }
        }
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

    @JvmStatic
    @BindingAdapter("draweeViewCustomization")
    fun setDraweeViewCustomization(simpleDraweeView: SimpleDraweeView, customization: ToolbarType.IconCustomization?) {
        if (null == customization) {
            simpleDraweeView.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
            simpleDraweeView.hierarchy.roundingParams = null
        } else {
            customization.applyNewStyle(simpleDraweeView)
        }
    }

    private fun getResourceDrawable(context: Context, resourcePath: String): Drawable? {
        if (resourcePath.startsWith(UriUtil.LOCAL_RESOURCE_SCHEME)) {
            try {
                val resourceIdAsString = resourcePath.substring(UriUtil.LOCAL_RESOURCE_SCHEME.length + 2)
                val resourceId = Integer.parseInt(resourceIdAsString)
                return AppCompatResources.getDrawable(context, resourceId)
            } catch (e: Throwable) {
                // pass
            }
        }
        return null
    }
}

fun Int.toFrescoImagePath(): String = UriUtil.LOCAL_RESOURCE_SCHEME + ":/$this"