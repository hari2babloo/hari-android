package io.scal.ambi.extensions.binding

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.scal.ambi.BR
import io.scal.ambi.extensions.view.ToolbarType

object ToolbarBinder {

    @JvmStatic
    @BindingAdapter("toolbarContent")
    fun bindToolbarContent(viewGroup: ViewGroup, content: ToolbarType.Content?) {
        viewGroup.removeAllViews()
        content?.run {
            val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(viewGroup.context), layoutId, viewGroup, false)
            binding.setVariable(BR.viewModel, content.screenModel)
            viewGroup.addView(binding.root)
        }
    }

    @JvmStatic
    @BindingAdapter("toolbarContentExpand")
    fun setLayoutWidth(view: View, expandView: Boolean?) {
        if (null != expandView && expandView) {
            view.layoutParams
                .run {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    view.layoutParams = this
                }
        } else {
            view.layoutParams
                .run {
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    view.layoutParams = this
                }
        }
    }
}