package io.scal.ambi.extensions.binding.binders

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import io.scal.ambi.ui.global.base.adapter.SpinnerAdapterSimple

object SpinnerBinder {

    @JvmStatic
    @BindingAdapter(value = ["spinnerViewModels", "viewModelLayoutId", "viewModelDropdownLayoutId", "spinnerSelectedViewModel", "spinnerSelectionChanged"],
                    requireAll = false)
    fun <T> setSpinnerAdapter(spinner: Spinner,
                              viewModels: List<T>?,
                              layoutId: Int?,
                              dropdownLayoutId: Int?,
                              selectedItem: T?,
                              itemPositionChanged: InverseBindingListener?) {
        if (null == viewModels || null == layoutId) {
            spinner.adapter = null
        } else {
            @Suppress("UseExpressionBody", "UNCHECKED_CAST")
            var adapter = spinner.adapter as? SpinnerAdapterSimple<T>
            if (null == adapter) {
                adapter = SpinnerAdapterSimple(layoutId, dropdownLayoutId)
                spinner.adapter = adapter
            }

            adapter.updateData(viewModels)

            selectedItem?.run { spinner.setSelection(viewModels.indexOf(this)) }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>) {
                    itemPositionChanged?.onChange()
                }

                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    itemPositionChanged?.onChange()
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @InverseBindingAdapter(attribute = "spinnerSelectedViewModel", event = "spinnerSelectionChanged")
    fun <T> getSpinnerSelection(spinner: Spinner): T? {
        return spinner.selectedItem as? T
    }
}