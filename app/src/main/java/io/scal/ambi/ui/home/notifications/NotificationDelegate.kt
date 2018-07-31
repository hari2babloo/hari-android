package io.scal.ambi.ui.home.notifications

import com.ambi.work.R
import com.ambi.work.databinding.ItemNotificationBinding

internal class NotificationDelegate(viewModel: INotificationViewModel) :
    NotificationAdapterDelegateBase<ItemNotificationBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_notification

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is NotificationData
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNotificationBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? NotificationData
        super.onBindViewHolder(items, position, binding, payloads)
    }
}