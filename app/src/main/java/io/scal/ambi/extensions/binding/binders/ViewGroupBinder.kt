package io.scal.ambi.extensions.binding.binders

import android.arch.lifecycle.ViewModel
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.scal.ambi.BR
import io.scal.ambi.R

object ViewGroupBinder {

    @JvmStatic
    @BindingAdapter("viewGroupModels", "viewModelLayoutId")
    fun setDetailViews(viewGroup: ViewGroup, viewModels: List<ViewModel>?, layoutId: Int?) {
        if (null == viewModels || null == layoutId) {
            viewGroup.removeAllViews()
        } else {
            val layoutInflater by lazy { LayoutInflater.from(viewGroup.context) }

            removeOldViews(viewGroup, viewModels)

            for (i in viewModels.indices) {
                val viewModel = viewModels[i]
                val view = getCurrentViewOfModel(viewGroup, viewModel)
                if (null == view) {
                    val viewDataBinding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, layoutId, viewGroup, false)
                    val child = viewDataBinding.root
                    child.setTag(R.id.id_screen_model, viewModel)
                    viewGroup.addView(child, i)
                    viewDataBinding.setVariable(BR.viewModel, viewModel)
                    viewDataBinding.executePendingBindings()
                } else {
                    if (i != viewGroup.indexOfChild(view)) {
                        val hasFocus = view.hasFocus()
                        viewGroup.removeView(view)
                        viewGroup.addView(view, i)
                        if (hasFocus) {
                            view.requestFocus()
                        }
                    }
                }
            }
        }
    }

    private fun removeOldViews(viewGroup: ViewGroup, detailScreenModels: List<ViewModel>) {
        var i = 0
        while (i < viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            val screenModel = getCurrentModelOfView(child)
            if (detailScreenModels.contains(screenModel)) {
                i++
            } else {
                viewGroup.removeView(child)
            }
        }
    }

    private fun <T> getCurrentViewOfModel(viewGroup: ViewGroup, screenModel: T): View? {
        var i = 0
        val count = viewGroup.childCount
        while (i < count) {
            val child = viewGroup.getChildAt(i)
            if (screenModel == getCurrentModelOfView(child)) {
                return child
            }
            ++i
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    private fun getCurrentModelOfView(child: View): ViewModel = child.getTag(R.id.id_screen_model) as ViewModel
}