package io.scal.ambi.ui.home.chat.list

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.scal.ambi.ui.home.chat.list.data.ElementChatListFilter

class ChatFilterModel constructor(private val allFilters: List<ElementChatListFilter>) {

    internal val selectedFilter = ObservableField<ElementChatListFilter>()
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

class ChatFilterElementViewModel(val elementChatListFilter: ElementChatListFilter,
                                 val selected: Boolean) : ViewModel()