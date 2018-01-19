package io.scal.ambi.extensions.binding.binders

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambi.work.BR
import com.ambi.work.R

object ViewGroupBinder {

    @JvmStatic
    @BindingAdapter(value = ["viewGroupModels", "viewModelLayoutId", "viewModelClickListener"], requireAll = false)
    fun <T> setDetailViews(viewGroup: ViewGroup, viewModels: List<T>?, layoutId: Int?, modelClickListener: ViewModelClickListener?) {
        if (null == viewModels || null == layoutId) {
            viewGroup.removeAllViews()
        } else {
            val layoutInflater by lazy { LayoutInflater.from(viewGroup.context) }

            removeOldViews(viewGroup, viewModels)

            for (i in viewModels.indices) {
                val viewModel = viewModels[i]
                val view = getCurrentViewOfModel(viewGroup, viewModel)
                val viewIndex = view?.let { viewGroup.indexOfChild(it) } ?: -1
                if (-1 == viewIndex || viewIndex < i) {
                    val viewDataBinding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, layoutId, viewGroup, false)
                    val child = viewDataBinding.root
                    child.setTag(R.id.id_screen_model, viewModel)
                    viewGroup.addView(child, i)
                    modelClickListener?.run { child.setOnClickListener { onModelClicked(i) } }
                    viewDataBinding.setVariable(BR.viewModel, viewModel)
                } else {
                    if (i != viewIndex) {
                        val hasFocus = view!!.hasFocus()
                        viewGroup.removeView(view)
                        viewGroup.addView(view, i)
                        modelClickListener?.run { view.setOnClickListener { onModelClicked(i) } }
                        if (hasFocus) {
                            view.requestFocus()
                        }
                    }
                }
            }
        }
    }

    private fun <T> removeOldViews(viewGroup: ViewGroup, detailScreenModels: List<T>) {
        var i = 0
        while (i < viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            val screenModel = getCurrentModelOfView<T>(child)
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
    private fun <T> getCurrentModelOfView(child: View): T? = child.getTag(R.id.id_screen_model) as? T

    interface ViewModelClickListener {

        fun onModelClicked(position: Int)
    }
}