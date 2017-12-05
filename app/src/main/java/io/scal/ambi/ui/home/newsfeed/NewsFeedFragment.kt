package io.scal.ambi.ui.home.newsfeed

import io.scal.ambi.R
import io.scal.ambi.databinding.FragmentNewsFeedBinding
import io.scal.ambi.ui.global.base.BaseNavigationFragment
import kotlin.reflect.KClass

class NewsFeedFragment : BaseNavigationFragment<NewsFeedViewModel, FragmentNewsFeedBinding>() {

    override val layoutId: Int = R.layout.fragment_news_feed
    override val viewModelClass: KClass<NewsFeedViewModel> = NewsFeedViewModel::class

}