package io.scal.ambi.ui.home.newsfeed.creation.polls

import android.os.Bundle
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.FragmentPollsCreationBinding
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.enableCascade
import io.scal.ambi.ui.global.base.fragment.BaseFragment
import kotlin.reflect.KClass

class PollsCreationFragment : BaseFragment<PollsCreationViewModel, FragmentPollsCreationBinding>() {

    override val layoutId: Int = R.layout.fragment_polls_creation
    override val viewModelClass: KClass<PollsCreationViewModel> = PollsCreationViewModel::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stateModel
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .map { !it.progress }
            .subscribe { binding.rootContainer.enableCascade(it) }
            .addTo(destroyViewDisposables)
    }
}