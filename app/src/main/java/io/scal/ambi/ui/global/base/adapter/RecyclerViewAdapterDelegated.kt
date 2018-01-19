package io.scal.ambi.ui.global.base.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager
import com.ambi.work.BR

abstract class RecyclerViewAdapterDelegated<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val delegatesManager = AdapterDelegatesManager<List<T>>()

    protected open var dataList: List<T> = emptyList()

    protected fun addDelegate(delegate: AdapterDelegate<List<T>>) {
        delegatesManager.addDelegate(delegate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegatesManager.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(dataList, position, holder)
    }

    override fun getItemViewType(position: Int): Int =
        delegatesManager.getItemViewType(dataList, position)

    override fun getItemCount(): Int = dataList.size

    override fun onViewRecycled(holder: RecyclerView.ViewHolder?) {
/*
        (holder as? AdapterDelegateBase.BindingViewHolder<*>)?.run {
            holder.binding.setVariable(BR.viewModel, null)
            holder.binding.setVariable(BR.element, null)
            holder.binding.executePendingBindings()
        }
*/

        super.onViewRecycled(holder)
    }
}