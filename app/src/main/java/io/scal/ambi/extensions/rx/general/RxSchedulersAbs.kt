package io.scal.ambi.extensions.rx.general

import io.reactivex.*
import io.reactivex.schedulers.Schedulers

abstract class RxSchedulersAbs {

    abstract val mainThreadScheduler: Scheduler

    abstract val ioScheduler: Scheduler

    abstract val computationScheduler: Scheduler

    val immediateScheduler = Schedulers.trampoline()

    public fun <T> getIOToMainTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { objectObservable ->
            objectObservable
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler)
        }
    }

    fun <T> getIOToMainTransformerSingle(): SingleTransformer<T, T> {
        return SingleTransformer { objectObservable ->
            objectObservable
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler)
        }
    }

    fun <T> getIOToMainTransformerFlowable(): FlowableTransformer<T, T> {
        return FlowableTransformer { objectObservable ->
            objectObservable
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler)
        }
    }

    val ioToMainTransformerCompletable: CompletableTransformer
        get() = CompletableTransformer { objectObservable ->
            objectObservable
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler)
        }

    fun <T> getComputationToMainTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { objectObservable ->
            objectObservable
                .subscribeOn(computationScheduler)
                .observeOn(mainThreadScheduler)
        }
    }

    fun <T> getComputationToMainTransformerSingle(): SingleTransformer<T, T> {
        return SingleTransformer { objectObservable ->
            objectObservable
                .subscribeOn(computationScheduler)
                .observeOn(mainThreadScheduler)
        }
    }

}
