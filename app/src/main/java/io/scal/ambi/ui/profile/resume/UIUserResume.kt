package io.scal.ambi.ui.profile.resume

import io.scal.ambi.extensions.binding.observable.ObservableString

data class UIUserResume(val pitchInfo: String,
                        val pitchEditInfo: ObservableString,
                        val pitchEditing: Boolean)