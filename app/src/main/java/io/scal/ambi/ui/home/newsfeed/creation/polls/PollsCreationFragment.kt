package io.scal.ambi.ui.home.newsfeed.creation.polls

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.R
import io.scal.ambi.databinding.FragmentPollsCreationBinding
import io.scal.ambi.entity.User
import io.scal.ambi.entity.feed.PollsEndsTime
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.enableCascade
import io.scal.ambi.ui.global.base.adapter.SimpleSpinnerAdapter
import io.scal.ambi.ui.global.base.fragment.BaseFragment
import kotlin.reflect.KClass

class PollsCreationFragment : BaseFragment<PollsCreationViewModel, FragmentPollsCreationBinding>() {

    override val layoutId: Int = R.layout.fragment_polls_creation
    override val viewModelClass: KClass<PollsCreationViewModel> = PollsCreationViewModel::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStateModel()
        initAsUserSpinner()
        initPollEndsSpinner()
    }

    private fun initAsUserSpinner() {
        val asUserSpinnerAdapter = SimpleSpinnerAdapter<User>(R.layout.item_as_user_spinner)

        binding.sAsUser.adapter = asUserSpinnerAdapter
        binding.sAsUser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.selectAsUser(parent.getItemAtPosition(position) as User)
            }
        }

        viewModel.dataStateModel
            .toObservable()
            .filter { it is PollsCreationDataState.Data }
            .map { it as PollsCreationDataState.Data }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                asUserSpinnerAdapter.updateData(it.asUsers)
                binding.sAsUser.setSelection(it.asUsers.indexOf(it.selectedAsUser))
            }
            .addTo(destroyViewDisposables)
    }

    private fun initPollEndsSpinner() {
        val pollEndsAdapter = SimpleSpinnerAdapter<PollsEndsTime>(R.layout.item_poll_ends_spinner, R.layout.item_poll_ends_spinner_dropdown)

        binding.sPollEnds.adapter = pollEndsAdapter
        binding.sPollEnds.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val pollEndsTime = parent.getItemAtPosition(position) as PollsEndsTime
                if (pollEndsTime is PollsEndsTime.Custom) {
                    // todo
                } else {
                    viewModel.selectPollEnds(pollEndsTime)
                }
            }
        }

        viewModel.dataStateModel
            .toObservable()
            .filter { it is PollsCreationDataState.Data }
            .map { it as PollsCreationDataState.Data }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                pollEndsAdapter.updateData(it.pollDurations)
                binding.sPollEnds.setSelection(it.pollDurations.indexOf(it.selectedPollDuration))
            }
            .addTo(destroyViewDisposables)
    }

    private fun initStateModel() {
        viewModel.progressStateModel
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.rootContainer.enableCascade(!it.progress)
                binding.invalidateAll()
            }
            .addTo(destroyViewDisposables)

        var snackBar: Snackbar? = null
        viewModel.errorStateModel
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                snackBar?.dismiss()
                when (it) {
                    PollsCreationErrorState.NoError       -> snackBar = null
                    is PollsCreationErrorState.Error      -> Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    is PollsCreationErrorState.ErrorFatal -> {
                        snackBar = Snackbar.make(binding.rootContainer, it.message, Snackbar.LENGTH_INDEFINITE)
                        snackBar!!.setAction(R.string.text_retry, { viewModel.reload() })
                        snackBar!!.show()
                    }
                }
            }
            .addTo(destroyViewDisposables)
    }
}