package io.scal.ambi.model.repository.data.chat.utils

import com.twilio.chat.CallbackListener
import com.twilio.chat.ErrorInfo
import com.twilio.chat.StatusListener
import io.reactivex.CompletableEmitter
import io.reactivex.SingleEmitter

internal class TwilioCallbackSingle<T>(private val singleEmitter: SingleEmitter<T>, val name: String?) : CallbackListener<T>() {

    override fun onSuccess(p0: T) {
        if (!singleEmitter.isDisposed) {
            singleEmitter.onSuccess(p0)
        }
    }

    override fun onError(errorInfo: ErrorInfo) {
        if (!singleEmitter.isDisposed) {
            singleEmitter.onError(IllegalStateException("can perform operation for $name. code: ${errorInfo.code}, message: ${errorInfo.message}"))
        }
    }
}

internal class TwilioCallbackCompletable(private val completableEmitter: CompletableEmitter, val name: String?) : StatusListener() {

    override fun onSuccess() {
        if (!completableEmitter.isDisposed) {
            completableEmitter.onComplete()
        }
    }

    override fun onError(errorInfo: ErrorInfo) {
        if (!completableEmitter.isDisposed) {
            completableEmitter.onError(IllegalStateException("can perform operation for $name. code: ${errorInfo.code}, message: ${errorInfo.message}"))
        }
    }
}