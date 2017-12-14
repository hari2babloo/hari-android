package io.scal.ambi.ui.home.chat.list

import android.content.Context
import io.scal.ambi.ui.global.search.SearchViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

internal class ChatSearchViewModel @Inject constructor(context: Context,
                                                       router: Router): SearchViewModel(context, router){
}