package io.scal.ambi.ui.home.newsfeed.audience

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.ambi.work.R
import com.ambi.work.databinding.ActivityAudienceSelectionBinding
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import kotlin.reflect.KClass

class AudienceSelectionActivity : BaseToolbarActivity<AudienceSelectionViewModel, ActivityAudienceSelectionBinding>() {

    override val layoutId: Int = R.layout.activity_audience_selection
    override val viewModelClass: KClass<AudienceSelectionViewModel> = AudienceSelectionViewModel::class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initRecyclerView()
    }

    private fun initToolbar() {
        setToolbarType(ToolbarType(IconImage(R.drawable.ic_close),
                                   Runnable { onBackPressed() },
                                   ToolbarType.TitleContent(getString(R.string.title_audience_selection)),
                                   null,
                                   null))
    }

    private fun initRecyclerView() {
        binding.rvAudiences.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvAudiences.adapter = AudienceSelectionAdapter(viewModel)
    }

    companion object {

        internal const val EXTRA_SELECTED_AUDIENCE = "selectedAudience"

        fun createScreen(context: Context, selectedAudience: Audience?): Intent =
            Intent(context, AudienceSelectionActivity::class.java)
                .putExtra(EXTRA_SELECTED_AUDIENCE, selectedAudience)
    }
}