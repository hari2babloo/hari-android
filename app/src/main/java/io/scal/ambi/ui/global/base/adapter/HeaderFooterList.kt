package io.scal.ambi.ui.global.base.adapter

import android.support.v7.widget.RecyclerView

data class HeaderFooterList(private val headerElement: Any?,
                            private val footerElement: Any?,
                            private val data: List<Any>,
                            private var showHeader: Boolean = null != headerElement,
                            private var showFooter: Boolean = null != footerElement) : AbstractList<Any>() {

    init {
        if (null == headerElement && showHeader) {
            throw IllegalStateException("we can not show header without header element")
        }
        if (null == footerElement && showFooter) {
            throw IllegalStateException("we can not show footer without header element")
        }
    }

    override val size: Int
        get() = data.size +
            (if (showHeader) 1 else 0) +
            (if (showFooter) 1 else 0)

    override fun get(index: Int): Any =
        if (showHeader && 0 == index) {
            headerElement!!
        } else if (showFooter && size - 1 == index) {
            footerElement!!
        } else {
            data[index - (if (showHeader) 1 else 0)]
        }

    fun updateHeaderVisibility(show: Boolean, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        if (null != headerElement && showHeader != show) {
            showHeader = show
            if (show) {
                adapter.notifyItemInserted(0)
            } else {
                adapter.notifyItemRemoved(0)
            }
        }
    }

    fun updateFooterVisibility(show: Boolean, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        if (null != footerElement && showFooter != show) {
            showFooter = show
            if (show) {
                adapter.notifyItemInserted(size - 1)
            } else {
                adapter.notifyItemRemoved(size + 1)
            }
        }
    }

    fun hasSameData(data: List<Any>): Boolean {
        return data == this.data
    }
}