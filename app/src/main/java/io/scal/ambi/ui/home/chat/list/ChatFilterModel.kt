package io.scal.ambi.ui.home.chat.list

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.scal.ambi.ui.home.chat.list.data.UIChatListFilter

class ChatFilterModel constructor(private val allFilters: List<UIChatListFilter>) {

    internal val selectedFilter = ObservableField<UIChatListFilter>()
    var viewModels = ObservableField(generateViewModels())

    fun onFilterClicked(elementIndex: Int) {
        selectedFilter.set(allFilters[elementIndex])
        viewModels.set(generateViewModels())
    }

    private fun generateViewModels(): List<ChatFilterElementViewModel>? {
        val selectedFilter = selectedFilter.get()
        return if (null == selectedFilter) {
            null
        } else {
            allFilters.map { ChatFilterElementViewModel(it, it == selectedFilter) }
        }
    }
}

class ChatFilterElementViewModel(val elementChatListFilter: UIChatListFilter,
                                 val selected: Boolean) : ViewModel()