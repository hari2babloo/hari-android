package io.scal.ambi.ui.home.newsfeed.creation.polls

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.ambi.work.R
import com.ambi.work.databinding.FragmentPollsCreationBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.feed.PollEndsTime
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.enableCascade
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.adapter.SpinnerAdapterSimple
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.fragment.BaseFragment
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.global.picker.PickerViewController
import io.scal.ambi.ui.home.newsfeed.creation.FeedItemCreationActivity
import io.scal.ambi.ui.home.newsfeed.creation.ICreationFragment
import org.joda.time.DateTime
import org.joda.time.Duration
import kotlin.reflect.KClass

class PollsCreationFragment : BaseFragment<PollsCreationViewModel, FragmentPollsCreationBinding>(), ICreationFragment {

    override val layoutId: Int = R.layout.fragment_polls_creation
    override val viewModelClass: KClass<PollsCreationViewModel> = PollsCreationViewModel::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStateModel()
        initAsUserSpinner()
        initPollEndsSpinner()
        initBottomActions()
    }

    private fun initAsUserSpinner() {
        val asUserSpinnerAdapter = SpinnerAdapterSimple<User>(R.layout.item_as_user_spinner, R.layout.item_as_user_spinner_dropdown)

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

    private fun initBottomActions() {
        viewModel.bottomViewModel
            .initBottomActions(binding.etQuestion, (activity as FeedItemCreationActivity).pickerViewModel, (activity as PickerViewController))
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe()
            .addTo(destroyViewDisposables)
    }

    override fun setPickedFile(fileResource: FileResource, image: Boolean) {
        viewModel.bottomViewModel.setPickedFile(fileResource, image)
    }

    private fun initPollEndsSpinner() {
        val pollEndsAdapter = SpinnerAdapterSimple<PollEndsTime>(R.layout.item_poll_ends_spinner, R.layout.item_poll_ends_spinner_dropdown)

        binding.sPollEnds.adapter = pollEndsAdapter
        binding.sPollEnds.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val pollEndsTime = parent.getItemAtPosition(position) as PollEndsTime
                if (pollEndsTime is PollEndsTime.UserCustomDefault) {
                    onCustomDurationSelected(pollEndsTime.duration)
                } else if (pollEndsTime is PollEndsTime.UserCustom) {
                    onCustomDurationSelected(pollEndsTime.duration)
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

    private fun onCustomDurationSelected(initDuration: Duration) {
        val initMinDate = DateTime.now().plus(1)
        val initSelectedDate = initMinDate.plus(initDuration)
        val picker = DatePickerDialog(activity,
                                      DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                                          val nowDate = DateTime.now()
                                          val selectedDate = nowDate.withYear(year).withMonthOfYear(month + 1).withDayOfMonth(dayOfMonth)
                                          val duration = Duration(nowDate, selectedDate)
                                          viewModel.selectPollEnds(PollEndsTime.UserCustom(duration))
                                      },
                                      initSelectedDate.year,
                                      initSelectedDate.monthOfYear - 1,
                                      initSelectedDate.dayOfMonth)
        picker.datePicker.minDate = initMinDate.toDate().time
        picker.show()
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

        viewModel.errorStateModel.asErrorState(binding.rootContainer,
                                               { viewModel.reload() },
                                               {
                                                   when (it) {
                                                       is PollsCreationErrorState.NoError    -> ErrorState.NoError
                                                       is PollsCreationErrorState.Error      -> ErrorState.NonFatalError(it.message)
                                                       is PollsCreationErrorState.ErrorFatal -> ErrorState.FatalError(it.message)
                                                   }
                                               },
                                               destroyViewDisposables)
    }
}