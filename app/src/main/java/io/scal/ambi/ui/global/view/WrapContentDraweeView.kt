package io.scal.ambi.ui.global.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.net.Uri
import android.util.AttributeSet
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo


class WrapContentDraweeView : SimpleDraweeView {

    // we set a listener and update the view's aspect ratio depending on the loaded image
    private val listener = object : BaseControllerListener<ImageInfo>() {
        override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
            updateViewSize(imageInfo)
        }

        override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
            updateViewSize(imageInfo)
        }
    }

    constructor(context: Context?, hierarchy: GenericDraweeHierarchy?) : super(context, hierarchy)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun setImageURI(uri: Uri?, callerContext: Any?) {
        val controller = (controllerBuilder as PipelineDraweeControllerBuilder)
            .setControllerListener(listener)
            .setCallerContext(callerContext)
            .setUri(uri)
            .setOldController(controller)
            .build()
        setController(controller)
    }

    fun updateViewSize(imageInfo: ImageInfo?) {
        if (imageInfo != null) {
            aspectRatio = imageInfo.width.toFloat() / imageInfo.height
        }
    }

}