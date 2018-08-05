package io.scal.ambi.ui.home.classes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import com.ambi.work.R
import com.ambi.work.databinding.ActivityClassesDetailsBinding
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.global.base.FragmentSwitcher
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.home.classes.about.AboutFragment
import io.scal.ambi.ui.home.classes.members.MembersFragment
import kotlin.reflect.KClass

/**
 * Created by chandra on 02-08-2018.
 */
class ClassesDetailsActivity : BaseToolbarActivity<ClassesDetailsViewModel, ActivityClassesDetailsBinding>() {

    private lateinit var fragmentSwitcher: FragmentSwitcher

    override val layoutId: Int = R.layout.activity_classes_details
    override val viewModelClass: KClass<ClassesDetailsViewModel> = ClassesDetailsViewModel::class

    private var defaultToolbarType: ToolbarType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initTabbarListener()
    }

    private fun initToolbar() {
        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_back_white),
                Runnable { router.exit() },
                ToolbarType.TitleContent(""),
                null,
                null,
                null,
                null)

        defaultToolbarType!!.makeScrolling()
        setToolbarType(defaultToolbarType)
    }

    companion object {
        internal val EXTRA_CLASS_ITEM = "EXTRA_CLASS_ITEM"
        fun createScreen(context: Context, classItem: ClassesData?): Intent =
                Intent(context, ClassesDetailsActivity::class.java).putExtra(EXTRA_CLASS_ITEM,classItem!!)

    }

    private fun initTabbarListener(){
        fragmentSwitcher = FragmentSwitcher(supportFragmentManager,
                hashMapOf(
                        //Pair(R.id.tab_about, AboutFragment.createScreen(viewModel.classesDetails)::class),
                        Pair(R.id.tab_about, AboutFragment::class),
                        Pair(R.id.tab_discussion, Fragment::class),
                        Pair(R.id.tab_events, Fragment::class),
                        Pair(R.id.tab_members, MembersFragment::class),
                        Pair(R.id.tab_class_documents, Fragment::class),
                        Pair(R.id.tab_assignments, Fragment::class)
                )
        )
        fragmentSwitcher.showTab(R.id.tab_discussion)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                val args = Bundle()
                args.putSerializable(EXTRA_CLASS_ITEM, viewModel.classesDetails)
                fragmentSwitcher.showTab(Integer.valueOf(tab.tag.toString()),args)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

}