package io.scal.ambi.extensions.binding.models

class TextInputLayoutModel : TextInputObjectLayoutModel<String> {

    constructor()

    constructor(value: String?, errorMessage: String?) : super(value, errorMessage)

    constructor(value: String?, errorMessage: String?, showError: Boolean) : super(value, errorMessage, showError)

    override var value: String?
        get() = super.value.orEmpty()
        set(value) {
            super.value = value!!
        }
}