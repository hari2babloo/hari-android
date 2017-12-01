package io.scal.ambi.extensions.binding.models

import android.databinding.BaseObservable
import android.databinding.ObservableField
import java.io.Serializable

open class TextInputObjectLayoutModel<T> @JvmOverloads constructor(value: T? = null,
                                                                   private var mErrorMessage: String? = null,
                                                                   private var mShowError: Boolean = false) : BaseObservable(), Serializable {

    private val mValue: ObservableField<T> = ObservableField<T>(value)

    open var value: T?
        get() = mValue.get()
        set(value) {
            if (value != mValue.get()) {
                mValue.set(value)
                mShowError = false
                notifyChange()
            }
        }

    var errorMessage: String?
        get() = mErrorMessage
        set(errorMessage) {
            if (mErrorMessage != errorMessage) {
                mErrorMessage = errorMessage
                notifyChange()
            }
        }

    var isShowError: Boolean
        get() = mShowError
        set(showError) {
            if (mShowError != showError) {
                mShowError = showError
                notifyChange()
            }
        }
}