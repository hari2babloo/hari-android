package io.scal.ambi.ui.global.base

import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.android.databinding.library.baseAdapters.BR
import io.scal.ambi.R
import io.scal.ambi.ui.global.view.ToolbarType

abstract class BaseToolbarActivity<IViewModel : BaseViewModel, Binding : ViewDataBinding> : BaseActivity<IViewModel, Binding>() {

    open val toolbarTypeInitial: ToolbarType? = null
    private val toolbarType: ObservableField<ToolbarType> by lazy { ObservableField<ToolbarType>(toolbarTypeInitial) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.setVariable(BR.toolbarType, toolbarType)
        setSupportActionBar(binding.root.findViewById(R.id.tb))
    }

    fun updateToolbarType(newToolbarType: ToolbarType?) {
        toolbarType.set(newToolbarType)
    }
}