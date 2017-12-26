package io.scal.ambi.ui.home.chat.newmessage.chip

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import com.pchmn.materialchips.ChipsInput
import com.pchmn.materialchips.adapter.ChipsAdapter
import com.pchmn.materialchips.model.ChipInterface
import com.pchmn.materialchips.views.DetailedChipView
import com.pchmn.materialchips.views.FilterableListView
import io.scal.ambi.R
import java.lang.reflect.Field

class CustomChipsInput @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ChipsInput(context, attrs) {

    private val chipListField: Field = ChipsInput::class.java.getDeclaredField("mChipList")
    private val filterableListViewField: Field = ChipsInput::class.java.getDeclaredField("mFilterableListView")
    private val chipsAdapterField: Field = ChipsInput::class.java.getDeclaredField("mChipsAdapter")

    private val filterableListBackgroundColorField = ChipsInput::class.java.getDeclaredField("mFilterableListBackgroundColor")
    private val filterableListTextColorField = ChipsInput::class.java.getDeclaredField("mFilterableListTextColor")

    private var prevDetailedChipView: DetailedChipView? = null

    init {
        chipListField.isAccessible = true
        filterableListViewField.isAccessible = true
        chipsAdapterField.isAccessible = true
        filterableListBackgroundColorField.isAccessible = true
        filterableListTextColorField.isAccessible = true
    }

    override fun setFilterableList(list: MutableList<out ChipInterface>?) {
        chipListField.set(this, list)

        val filterableListView = CustomFilterableListView(context)
        filterableListView.build(list,
                                 this,
                                 filterableListBackgroundColorField.get(this) as? ColorStateList,
                                 filterableListTextColorField.get(this) as? ColorStateList)
        filterableListViewField.set(this, filterableListView)

        (chipsAdapterField.get(this) as ChipsAdapter).setFilterableListView(filterableListView)
    }

    override fun onTextChanged(text: CharSequence) {
        super.onTextChanged(text)

        (filterableListViewField.get(this) as? FilterableListView)?.filterList(text)
    }

    override fun addChip(chip: ChipInterface?) {
        removePreDetailedView()
        super.addChip(chip)
    }

    override fun addChip(id: Any?, icon: Drawable?, label: String?, info: String?) {
        removePreDetailedView()
        super.addChip(id, icon, label, info)
    }

    override fun addChip(icon: Drawable?, label: String?, info: String?) {
        removePreDetailedView()
        super.addChip(icon, label, info)
    }

    override fun addChip(id: Any?, iconUri: Uri?, label: String?, info: String?) {
        removePreDetailedView()
        super.addChip(id, iconUri, label, info)
    }

    override fun addChip(iconUri: Uri?, label: String?, info: String?) {
        removePreDetailedView()
        super.addChip(iconUri, label, info)
    }

    override fun addChip(label: String?, info: String?) {
        removePreDetailedView()
        super.addChip(label, info)
    }

    override fun isShowChipDetailed(): Boolean {
        return super.isShowChipDetailed()
    }

    override fun getDetailedChipView(chip: ChipInterface?): DetailedChipView {
        removePreDetailedView()

        val detailedView = super.getDetailedChipView(chip)
        detailedView.findViewById<View>(R.id.content).setOnClickListener { removePreDetailedView() }
        prevDetailedChipView = detailedView
        return detailedView
    }

    private fun removePreDetailedView() {
        val detailedView = prevDetailedChipView
        if (null != detailedView && detailedView.visibility == View.VISIBLE) {
            detailedView.fadeOut()
        }
        prevDetailedChipView = null
    }
}