package io.scal.ambi.extensions.rx.general

import io.reactivex.Scheduler

class RxSchedulersTest : RxSchedulersAbs() {

    override val mainThreadScheduler: Scheduler = immediateScheduler

    override val ioScheduler: Scheduler = immediateScheduler

    override val computationScheduler: Scheduler = immediateScheduler
}