package io.scal.ambi.ui.home.chat.newmessage.chip

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.pchmn.materialchips.ChipsInput
import com.pchmn.materialchips.ChipsInput.ChipValidator
import com.pchmn.materialchips.adapter.ChipsAdapter
import com.pchmn.materialchips.adapter.FilterableAdapter
import com.pchmn.materialchips.model.ChipInterface
import com.pchmn.materialchips.util.ActivityUtil
import com.pchmn.materialchips.views.DetailedChipView
import com.pchmn.materialchips.views.FilterableListView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import com.ambi.work.R
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.binding.toObservable
import java.lang.reflect.Field

class CustomChipsInput @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ChipsInput(context, attrs) {

    private val chipListField: Field = ChipsInput::class.java.getDeclaredField("mChipList")
    private val filterableListViewField: Field = ChipsInput::class.java.getDeclaredField("mFilterableListView")
    private val chipsAdapterField: Field = ChipsInput::class.java.getDeclaredField("mChipsAdapter")

    private val filterableListBackgroundColorField = ChipsInput::class.java.getDeclaredField("mFilterableListBackgroundColor")
    private val filterableListTextColorField = ChipsInput::class.java.getDeclaredField("mFilterableListTextColor")

    private var prevDetailedChipView: DetailedChipView? = null

    private val selectedChipList = OptimizedObservableArrayList<ChipInterface>()

    private val selectedTextSubject = BehaviorSubject.create<String>()

    init {
        chipListField.isAccessible = true
        filterableListViewField.isAccessible = true
        chipsAdapterField.isAccessible = true
        filterableListBackgroundColorField.isAccessible = true
        filterableListTextColorField.isAccessible = true

        addChipsListener(object : ChipsListener {
            override fun onChipAdded(p0: ChipInterface, p1: Int) {
                if (!selectedChipList.contains(p0)) {
                    selectedChipList.add(p0)
                    validateViewSize()
                }
            }

            override fun onChipRemoved(p0: ChipInterface, p1: Int) {
                selectedChipList.remove(p0)
            }

            override fun onTextChanged(p0: CharSequence?) {}
        })

        chipValidator = ChipValidator { chipInterface1, chipInterface2 -> chipInterface1.id == chipInterface2.id }
    }

    fun observeSelectedList(): Observable<List<ChipInterface>> = selectedChipList.toObservable()
    fun observeSelectedText(): Observable<String> = selectedTextSubject

    fun setSelectedChipList(selectedChips: List<ChipInterface>) {
        val itemsToDelete = selectedChipList.filter { !selectedChips.contains(it) }
        itemsToDelete.forEach { removeChip(it) }
        val itemsToAdd = selectedChips.filter { !selectedChipList.contains(it) }
        itemsToAdd.forEach { addChip(it) }
    }

    override fun setFilterableList(list: MutableList<out ChipInterface>?) {
        chipListField.set(this, list)

        (filterableListViewField.get(this) as? View)?.let { postDelayed({ (it.parent as? ViewGroup)?.removeView(it) }, 100) }
        val filterableListView = CustomFilterableListView(context)
        filterableListView.build(list,
                                 this,
                                 filterableListBackgroundColorField.get(this) as? ColorStateList,
                                 filterableListTextColorField.get(this) as? ColorStateList)
        filterableListViewField.set(this, filterableListView)

        (chipsAdapterField.get(this) as ChipsAdapter).setFilterableListView(filterableListView)

        selectedChipList.forEach { onChipAdded(it, selectedChipList.size) } // we rebuild list and should notify about the changes

        (filterableListViewField.get(this) as? CustomFilterableListView)?.filterList(selectedTextSubject.value ?: "")

        validateViewSize()
    }

    private fun validateViewSize() {
        ActivityUtil.scanForActivity(context).window.decorView.requestLayout()
        post {
            (filterableListViewField.get(this) as? CustomFilterableListView)?.run {
                visibility = View.GONE
                fadeIn()
            }
        }
    }

    fun showPopup() {
        (filterableListViewField.get(this) as? CustomFilterableListView)?.fadeIn()
    }

    fun hidePopup() {
        (filterableListViewField.get(this) as? CustomFilterableListView)?.doRealFadeOut()
    }

    override fun onTextChanged(text: CharSequence) {
        selectedTextSubject.onNext(text.toString())

        val filterableListView = filterableListViewField.get(this) as? FilterableListView
        filterableListViewField.set(this, null)
        super.onTextChanged(text)
        filterableListViewField.set(this, filterableListView)

        filterableListView?.filterList(text)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        (filterableListViewField.get(this) as? CustomFilterableListView)?.isEnabled = enabled
    }

    override fun onChipRemoved(chip: ChipInterface?, size: Int) {
        super.onChipRemoved(chip, size)

        (filterableListViewField.get(this) as? CustomFilterableListView)?.run {
            val adapter = FilterableListView::class.java.getDeclaredField("mAdapter").let {
                it.isAccessible = true
                it.get(this) as? FilterableAdapter
            }
            val filterableList = adapter?.javaClass?.getDeclaredField("mFilteredList")?.let {
                it.isAccessible = true
                it.get(adapter) as? MutableList<ChipInterface>
            }
            filterableList?.remove(chip)
            adapter?.notifyDataSetChanged()

            filterList(selectedTextSubject.value ?: "")
        }
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