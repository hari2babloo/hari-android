package io.scal.ambi.ui.global.model

import io.reactivex.*
import io.reactivex.subjects.PublishSubject
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class DynamicUserChoicer<Uid>(private val rxSchedulersAbs: RxSchedulersAbs,
                              private val choiceExecutor: (Uid, Action) -> Completable,
                              private val uidExtractor: (Uid) -> Any = { it -> it as Any }) {

    private val userChoices = PublishSubject.create<UserChoice<Uid>>()

    fun activate(): Flowable<Pair<Uid, Action>> {
        return userChoices
            .observeOn(rxSchedulersAbs.computationScheduler)
            .toFlowable(BackpressureStrategy.BUFFER)
            // we group all action for a groups, so that we will have a list of actions for a specific item
            .groupBy { t -> uidExtractor.invoke(t.uid) }
            // will do a setup on main thread to disable any concurrent things
            .observeOn(rxSchedulersAbs.mainThreadScheduler)
            .flatMap {
                // we need it to fallback on error
                val prevServerState = AtomicReference<Action>()
                // we need this to block any change event while we process previous
                val barrier = AtomicBoolean(false)
                // we need this to find if there are some more events to work with
                val quire = mutableListOf<UserChoice<Uid>>()
                // will fire every interval so we can pack user actions
                val readyPublisher = Observable.interval(700, TimeUnit.MILLISECONDS)
                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
                    .switchMap { if (barrier.get()) Observable.never() else Observable.just(it) } // we will not have an interval is blocked

                it
                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
                    .doOnEach { quire.add(it.value!!) } // we save each user event
                    .buffer(readyPublisher.toFlowable(BackpressureStrategy.LATEST)) // buffer to have a list of actions every interval
                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
                    .filter { it.isNotEmpty() } // list can be empty if no actions
                    .doOnEach { barrier.set(true) } // we should block interval if we have actions to do
                    .flatMap {
                        quire.removeAll(it) // we are working with this actions, so remove them from the list

                        prevServerState.compareAndSet(null, it.first().oldAction) // store failback state if there is no such
                        val newValue = it.last()

                        val executableAction: Maybe<Pair<Uid, Action>> =
                            if (newValue.newAction == prevServerState.get()) {
                                Maybe.empty() // new value is already populated
                            } else {
                                choiceExecutor
                                    .invoke(newValue.uid, newValue.newAction) // sending to server
                                    .subscribeOn(rxSchedulersAbs.ioScheduler)
                                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
                                    .andThen( // server accept our change
                                        Maybe.fromAction<Pair<Uid, Action>> {
                                            Timber.i("${newValue.newAction} applied")
                                            prevServerState.set(null) // remove failback value, because we don't need it any more
                                        }
                                    )
                                    .onErrorResumeNext( // error during server update. we should reset UI value
                                        Maybe.fromCallable<Pair<Uid, Action>> {
                                            val oldAction = prevServerState.get()!!
                                            if (quire.isEmpty()) {
                                                // we have failback value and don't have pending requests
                                                Timber.i("${newValue.newAction} applied FAILED. fail back to $oldAction")

                                                Pair(newValue.uid, oldAction)
                                            } else {
                                                // we have pending requests
                                                Timber.i("${newValue.newAction} applied FAILED. we have other items to try with")

                                                null
                                            }
                                        }
                                    )
                            }

                        executableAction
                            .observeOn(rxSchedulersAbs.mainThreadScheduler)
                            .doFinally { barrier.set(false) }// we need to release the barrier
                            .toFlowable()
                    }
            }
    }

    fun changeUserChoice(elementUid: Uid, newAction: Action, oldAction: Action) {
        userChoices.onNext(UserChoice(elementUid, newAction, oldAction))
    }

    private class UserChoice<out Uid>(val uid: Uid, val newAction: Action, val oldAction: Action) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is UserChoice<*>) return false

            if (uid != other.uid) return false

            return true
        }

        override fun hashCode(): Int {
            return uid?.hashCode() ?: 0
        }
    }

    enum class Action {
        LIKE,
        DISLIKE,
        NONE
    }
}