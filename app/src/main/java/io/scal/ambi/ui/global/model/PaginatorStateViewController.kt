package io.scal.ambi.ui.global.model

import android.content.Context
import android.databinding.ObservableField
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage

abstract class PaginatorStateViewController<in Items, out ProgressState, out ErrorState>(
    private val context: Context,
    private val progressState: ObservableField<ProgressState>,
    private val errorState: ObservableField<ErrorState>
) : Paginator.ViewController<Items> {

    override fun showEmptyProgress(show: Boolean) {
        if (show) progressState.set(generateProgressEmptyState())
        else progressState.set(generateProgressNoState())
    }

    override fun showRefreshProgress(show: Boolean) {
        if (show) progressState.set(generateProgressRefreshState())
        else progressState.set(generateProgressNoState())
    }

    override fun showPageProgress(show: Boolean) {
        if (show) progressState.set(generateProgressPageState())
        else progressState.set(generateProgressNoState())
    }

    abstract fun generateProgressEmptyState(): ProgressState
    abstract fun generateProgressNoState(): ProgressState
    abstract fun generateProgressRefreshState(): ProgressState
    abstract fun generateProgressPageState(): ProgressState


    override fun showEmptyError(show: Boolean, error: Throwable?) {
        if (show && null != error) {
            errorState.set(generateErrorFatal(error.toGoodUserMessage(context)))
        } else {
            errorState.set(generateErrorNo())
        }
    }

    override fun showErrorMessage(error: Throwable) {
        errorState.set(generateErrorNonFatal(error.toGoodUserMessage(context)))
        errorState.set(generateErrorNo())
    }

    abstract fun generateErrorFatal(message: String): ErrorState
    abstract fun generateErrorNonFatal(message: String): ErrorState
    abstract fun generateErrorNo(): ErrorState
}