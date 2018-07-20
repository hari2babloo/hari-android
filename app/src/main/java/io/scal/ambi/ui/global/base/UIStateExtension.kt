package io.scal.ambi.ui.global.base

import android.databinding.ObservableField
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import com.ambi.work.R
import io.scal.ambi.extensions.binding.toObservable

fun <T> ObservableField<T>.asErrorState(rootView: View, refreshFatal: () -> Unit, errorState: (T) -> ErrorState, disposables: CompositeDisposable) {
    var snackBar: Snackbar? = null

    toObservable()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            snackBar?.dismiss()
            val convertedState = errorState.invoke(it)
            when (convertedState) {
                is ErrorState.NoError       -> snackBar = null
                is ErrorState.FatalError    -> {
                    snackBar = Snackbar.make(rootView, convertedState.error, Snackbar.LENGTH_SHORT)
                    snackBar!!.setAction(R.string.text_retry, { refreshFatal() })
                    snackBar!!.show()
                }
                is ErrorState.NonFatalError ->
                    Toast.makeText(rootView.context, convertedState.error, Toast.LENGTH_SHORT).show()
            }
        }
        .addTo(disposables)
}

sealed class ErrorState {
    object NoError : ErrorState()
    class FatalError(val error: String) : ErrorState()
    class NonFatalError(val error: String) : ErrorState()
}

fun <T> ObservableField<T>.asProgressStateSrl(srl: SwipeRefreshLayout,
                                              adapterPageProgress: (Boolean) -> Unit,
                                              progressState: (T) -> ProgressState,
                                              disposables: CompositeDisposable) {
    toObservable()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            adapterPageProgress.invoke(false)
            val convertedState = progressState.invoke(it)
            when (convertedState) {
                is ProgressState.EmptyProgress   -> srl.isRefreshing = true
                is ProgressState.PageProgress    -> adapterPageProgress.invoke(true)
                is ProgressState.RefreshProgress -> srl.isRefreshing = true
                is ProgressState.NoProgress      -> srl.isRefreshing = false
            }
        }
        .addTo(disposables)
}

sealed class ProgressState {
    object NoProgress : ProgressState()
    object EmptyProgress : ProgressState()
    object PageProgress : ProgressState()
    object RefreshProgress : ProgressState()
}