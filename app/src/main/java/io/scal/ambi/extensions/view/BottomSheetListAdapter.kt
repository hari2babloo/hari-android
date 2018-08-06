package io.scal.ambi.extensions.view

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ambi.work.R

/**
 * Created by chandra on 06-08-2018.
 */
class BottomSheetListAdapter(val items : List<String>, val context: Context) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = convertView
        if (v == null) {
            val vi = context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(R.layout.item_bottom_sheet_values, null)
        }
        val product = items.get(position)
        if (product != null) {

            var tvTitle = (v!!.findViewById<TextView>(R.id.tvValue) as TextView)
            tvTitle?.setText(product)

            val fontResource = R.font.nicolas_desle_pantra_bold
            fontResource?.run {
                val typeface = ResourcesCompat.getFont(tvTitle.context, this)
                if (null != typeface) {
                    tvTitle.typeface = typeface
                }
            }
        }
        return v!!
    }

    override fun getItem(p0: Int): Any {
        return items.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}