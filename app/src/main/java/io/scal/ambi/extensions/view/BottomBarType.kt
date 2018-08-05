package io.scal.ambi.extensions.view

import android.content.Context
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ambi.work.R


/**
 * Created by chandra on 19-07-2018.
 */

open class BottomBarType: TabLayout {
    private val RESOURCE_NOT_FOUND = 0
    private var tabXmlResource: Int = 0
    private var tabLayoutId: Int = 0
    private var isImageTabBar: Boolean = false;
    private var tabScrollable: Boolean=false;

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialise(attrs,0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialise(attrs,defStyleAttr)
    }

    fun initialise(attrs: AttributeSet, defStyleAttr: Int){
        val ta = context.theme .obtainStyledAttributes(attrs, R.styleable.BottomBarType, defStyleAttr, 0)
        try
        {
            tabXmlResource = ta.getResourceId(R.styleable.BottomBarType_bb_tabXmlResource, 0)
            tabLayoutId = ta.getResourceId(R.styleable.BottomBarType_bb_tabItemLayout, 0)
            tabScrollable = ta.getBoolean(R.styleable.BottomBarType_bb_tabScrollable, false)
        }
        finally
        {
            ta.recycle()
        }

        if(tabScrollable){
            this.tabMode = TabLayout.MODE_SCROLLABLE
        }

        if (tabXmlResource != 0) {
            val parser = ResourceParser(getContext(), tabXmlResource)
            val tabs: MutableList<TabResourceItem>? = parser.parseTabs()

            for (i in 0 until tabs!!.size) {
                val view = LayoutInflater.from(this.context).inflate(tabLayoutId, null)

                if(tabs.get(i).drawableId==0){
                    isImageTabBar = false
                    var tvTabName = (view.findViewById<View>(R.id.tvTabName) as TextView)
                    tvTabName.visibility = View.VISIBLE
                    tvTabName.text = tabs.get(i).label
                    if(i==0){
                        if(view.findViewById<View>(R.id.tvSeperator)!=null){
                            var tvSeperator = view.findViewById<View>(R.id.tvSeperator) as View
                            tvSeperator.visibility = View.VISIBLE
                        }
                        tvTabName.isSelected = true
                    }
                    val fontResource = R.font.nicolas_desle_pantra_bold
                    fontResource?.run {
                        val typeface = ResourcesCompat.getFont(tvTabName.context, this)
                        if (null != typeface) {
                            tvTabName.typeface = typeface
                        }
                    }
                }else{
                    var tvTabIcon = (view.findViewById<View>(R.id.tvTabIcon) as ImageView)
                    tvTabIcon.visibility = View.VISIBLE
                    tvTabIcon.setImageResource(tabs.get(i).drawableId)
                    isImageTabBar = true
                    if(i==0){
                        if(view.findViewById<View>(R.id.tvSeperator)!=null){
                            var tvSeperator = view.findViewById<View>(R.id.tvSeperator) as View
                            tvSeperator.visibility = View.VISIBLE
                        }
                        tvTabIcon.isSelected = true
                    }
                }

                val newTab = this.newTab()
                newTab.tag = tabs.get(i).itemid
                newTab.customView = view
                this.addTab(newTab);
            }
        }
    }

    init {
        this.setTabGravity(TabLayout.GRAVITY_FILL);
        this.setTabMode(TabLayout.MODE_FIXED);
        this.setSelectedTabIndicatorColor(ContextCompat.getColor(this.context, android.R.color.transparent))

        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(isImageTabBar){
                    var tvTabIcon =tab.customView!!.findViewById<View>(R.id.tvTabIcon) as ImageView
                    tvTabIcon!!.isSelected = true
                }else{
                    var tvTabName =tab.customView!!.findViewById<View>(R.id.tvTabName) as TextView
                    tvTabName!!.isSelected = true
                }
                if(tab.customView!!.findViewById<View>(R.id.tvSeperator)!=null){
                    var tvSeperator = tab.customView!!.findViewById<View>(R.id.tvSeperator) as View
                    tvSeperator.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                if(isImageTabBar){
                    var tvTabIcon =tab.customView!!.findViewById<View>(R.id.tvTabIcon) as ImageView
                    tvTabIcon.isSelected = false
                }else{
                    var tvTabName =tab.customView!!.findViewById<View>(R.id.tvTabName) as TextView
                    tvTabName!!.isSelected = false
                }
                if(tab.customView!!.findViewById<View>(R.id.tvSeperator)!=null){
                    var tvSeperator = tab.customView!!.findViewById<View>(R.id.tvSeperator) as View
                    tvSeperator.visibility = View.INVISIBLE
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

}