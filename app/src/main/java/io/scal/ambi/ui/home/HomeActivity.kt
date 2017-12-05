package io.scal.ambi.ui.home

import android.content.Context
import android.content.Intent
import io.scal.ambi.R
import io.scal.ambi.databinding.ActivityHomeBinding
import io.scal.ambi.ui.global.BaseToolbarActivity
import io.scal.ambi.ui.global.search.SearchToolbarContent
import io.scal.ambi.ui.global.view.ToolbarType
import javax.inject.Inject
import kotlin.reflect.KClass

class HomeActivity : BaseToolbarActivity<HomeViewModel, ActivityHomeBinding>() {

    override val layoutId: Int = R.layout.activity_home
    override val viewModelClass: KClass<HomeViewModel> = HomeViewModel::class

    @Inject
    internal lateinit var searchViewModel: HomeSearchViewModel
    override val toolbarTypeInitial
        by lazy { ToolbarType(R.drawable.ic_ambi_logo_small, SearchToolbarContent(searchViewModel), R.drawable.ic_profile) }

    companion object {

        fun createScreen(context: Context) =
            Intent(context, HomeActivity::class.java)
    }
}