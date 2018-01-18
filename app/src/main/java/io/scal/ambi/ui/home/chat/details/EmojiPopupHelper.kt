package io.scal.ambi.ui.home.chat.details

import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiPopup
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import com.ambi.work.R
import io.scal.ambi.entity.EmojiKeyboardState
import java.util.concurrent.atomic.AtomicReference

internal class EmojiPopupHelper {

    fun activate(editText: EmojiEditText, observable: Observable<EmojiKeyboardState>): Observable<EmojiKeyboardState> {
        return observable
            .distinctUntilChanged()
            .switchMap { updateValue(editText, it) }
    }

    private fun updateValue(editText: EmojiEditText, state: EmojiKeyboardState): Observable<EmojiKeyboardState> {
        var emojiPopupInfo = editText.getTag(R.id.id_screen_model) as? PopupInformation

        if (state == emojiPopupInfo?.currentState) {
            return emojiPopupInfo.getObserver()
        }

        when (state) {
            EmojiKeyboardState.UNKNOWN -> {
                if (null == emojiPopupInfo?.emojiPopup) {
                    emojiPopupInfo = PopupInformation()

                    emojiPopupInfo.showKeyboard()
                    emojiPopupInfo.pushState(EmojiKeyboardState.UNKNOWN) // we don't know real state. lets thing that nothing
                } else {
                    if (emojiPopupInfo.emojiPopup!!.isShowing) {
                        emojiPopupInfo.showKeyboard()
                        emojiPopupInfo.pushState(EmojiKeyboardState.UNKNOWN)
                    } else {
                        emojiPopupInfo.showEmoji()
                        emojiPopupInfo.pushState(EmojiKeyboardState.EMOJI)
                    }
                }
            }
            EmojiKeyboardState.EMOJI   -> {
                if (null == emojiPopupInfo?.emojiPopup) {
                    emojiPopupInfo = buildPopup(editText)
                }

                emojiPopupInfo.showEmoji()
                emojiPopupInfo.pushState(EmojiKeyboardState.EMOJI)
            }
        }

        editText.setTag(R.id.id_screen_model, emojiPopupInfo)
        return emojiPopupInfo.getObserver()
    }

    private fun buildPopup(editText: EmojiEditText): PopupInformation {
        val keyboardState = BehaviorSubject.create<EmojiKeyboardState>()
        val ref = AtomicReference<PopupInformation>()
        val popup = EmojiPopup
            .Builder
            .fromRootView(editText.rootView.findViewById(android.R.id.content))
            .setOnEmojiPopupDismissListener { ref.get()?.pushState(EmojiKeyboardState.UNKNOWN) }
            .build(editText)

        val value = PopupInformation(keyboardState, popup)
        ref.set(value)
        return value
    }

    private data class PopupInformation(private val keyboardState: BehaviorSubject<EmojiKeyboardState> = BehaviorSubject.create(),
                                        val emojiPopup: EmojiPopup? = null) {

        var currentState: EmojiKeyboardState? = null

        fun pushState(state: EmojiKeyboardState) {
            if (currentState != state) {
                currentState = state
                keyboardState.onNext(state)
            }
        }

        fun showEmoji() {
            if (emojiPopup?.isShowing == false)
                emojiPopup.toggle()
        }

        fun showKeyboard() {
            if (emojiPopup?.isShowing == true)
                emojiPopup.toggle()
        }

        fun getObserver(): Observable<EmojiKeyboardState> = keyboardState.distinctUntilChanged()
    }
}