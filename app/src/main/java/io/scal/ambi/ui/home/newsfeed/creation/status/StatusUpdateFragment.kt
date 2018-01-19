package io.scal.ambi.ui.home.newsfeed.creation.status

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.ambi.work.R
import com.ambi.work.databinding.FragmentStatusUpdateBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
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
import kotlin.reflect.KClass

class StatusUpdateFragment : BaseFragment<StatusUpdateViewModel, FragmentStatusUpdateBinding>(), ICreationFragment {

    override val layoutId: Int = R.layout.fragment_status_update
    override val viewModelClass: KClass<StatusUpdateViewModel> = StatusUpdateViewModel::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStateModel()
        initAsUserSpinner()
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
            .filter { it is StatusUpdateDataState.Data }
            .map { it as StatusUpdateDataState.Data }
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
                                                       is StatusUpdateErrorState.NoError    -> ErrorState.NoError
                                                       is StatusUpdateErrorState.Error      -> ErrorState.NonFatalError(it.message)
                                                       is StatusUpdateErrorState.ErrorFatal -> ErrorState.FatalError(it.message)
                                                   }
                                               },
                                               destroyViewDisposables)
    }
}