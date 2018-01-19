package io.scal.ambi.extensions.binding.binders

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambi.work.BR
import io.scal.ambi.extensions.view.ToolbarType

object ToolbarBinder {

    @JvmStatic
    @BindingAdapter("toolbarContent")
    fun bindToolbarContent(viewGroup: ViewGroup, content: ToolbarType.Content?) {
        viewGroup.removeAllViews()
        content?.run {
            val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(viewGroup.context), layoutId, viewGroup, false)
            binding.setVariable(BR.viewModel, content.screenModel)
            binding.setVariable(BR.content, content)
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

    @JvmStatic
    @BindingAdapter("layout_collapseMode")
    fun setCollapseMode(toolbar: Toolbar, collapseMode: Int?) {
        val lp = toolbar.layoutParams as? CollapsingToolbarLayout.LayoutParams
        lp?.collapseMode = collapseMode ?: CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_OFF
    }

    @JvmStatic
    @BindingAdapter("layout_scrollFlags")
    fun setScrollFlags(toolbar: Toolbar, scrollFlags: Int?) {
        val lp = toolbar.layoutParams as? AppBarLayout.LayoutParams
        lp?.scrollFlags = scrollFlags ?: 0
    }
}