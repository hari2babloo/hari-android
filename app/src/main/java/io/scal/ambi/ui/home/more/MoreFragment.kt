package io.scal.ambi.ui.home.more
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.FragmentMoreBinding
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import io.scal.ambi.ui.home.newsfeed.list.adapter.NewsFeedAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

/**
 * Created by chandra on 23-07-2018.
 */

class MoreFragment: BaseNavigationFragment<MoreViewModel, FragmentMoreBinding>(){

    override val layoutId: Int = R.layout.fragment_more
    override val viewModelClass: KClass<MoreViewModel> = MoreViewModel::class
    private val adapter by lazy { MoreAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {

        val myList = ArrayList<MoreItem>()
        myList.add(MoreItem(R.drawable.ic_action_attachment,"Chandra"))
        adapter.updateData(myList)

        binding.tvMenu.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.tvMenu.setItemViewCacheSize(30)
        binding.tvMenu.isDrawingCacheEnabled = true
        binding.tvMenu.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }



}
