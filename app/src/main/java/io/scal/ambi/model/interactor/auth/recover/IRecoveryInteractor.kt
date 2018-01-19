package io.scal.ambi.model.interactor.auth.recover

import io.reactivex.Completable

interface IRecoveryInteractor {

    fun recover(email: String): Completable
}