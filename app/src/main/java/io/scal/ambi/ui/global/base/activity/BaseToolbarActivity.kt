package io.scal.ambi.ui.global.base.activity

import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.android.databinding.library.baseAdapters.BR
import com.ambi.work.R
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.extensions.view.ToolbarType

abstract class BaseToolbarActivity<IViewModel : BaseViewModel, Binding : ViewDataBinding> : BaseActivity<IViewModel, Binding>() {

    private val toolbarType: ObservableField<ToolbarType> by lazy { ObservableField<ToolbarType>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.setVariable(BR.toolbarType, toolbarType)
        setSupportActionBar(binding.root.findViewById(R.id.tb))
    }

    fun compareAndSetToolbarType(oldToolbarType: ToolbarType?, newToolbarType: ToolbarType?) {
        if (oldToolbarType == toolbarType.get()) {
            toolbarType.set(newToolbarType)
        }
    }

    fun setToolbarType(newToolbarType: ToolbarType?) {
        toolbarType.set(newToolbarType)
    }
}