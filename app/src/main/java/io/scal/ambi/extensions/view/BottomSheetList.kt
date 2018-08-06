package io.scal.ambi.extensions.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import com.ambi.work.R


/**
 * Created by chandra on 06-08-2018.
 */
class BottomSheetList: RelativeLayout{
    private var tabXmlResource: Int = 0
    private var listItemLayout: Int = 0
    private var title: String? = null;
    var rootView = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialise(attrs,0,context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialise(attrs,defStyleAttr,context)
    }

    private fun initialise(attrs: AttributeSet, defStyleAttr: Int, context: Context){
        val ta = context.theme .obtainStyledAttributes(attrs, R.styleable.BottomSheetList, defStyleAttr, 0)
        try
        {
            tabXmlResource = ta.getResourceId(R.styleable.BottomSheetList_bs_listXmlResource, 0)
            listItemLayout = ta.getResourceId(R.styleable.BottomSheetList_bs_listItemLayout, 0)
            title = ta.getString(R.styleable.BottomSheetList_bs_title)
        }
        finally
        {
            ta.recycle()
        }


        var inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.bottom_sheet,this,true)
        var tvTitle = (findViewById<TextView>(R.id.tvTitle) as TextView)
        tvTitle.setText(title)
        initRecyclerView()
    }

    private fun initRecyclerView(){
        var tvTabName = (findViewById<ListView>(R.id.rcv_list) as ListView)
        var animals = listOf<String>(
                "make appointment",
                "chat",
                "block"
        )
        tvTabName.adapter = BottomSheetListAdapter(animals, context)
    }
}