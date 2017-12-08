package io.scal.ambi.extensions.binding.binders

import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import io.scal.ambi.BR
import io.scal.ambi.R
import io.scal.ambi.databinding.ItemLikeUserNamesBinding
import io.scal.ambi.entity.User
import io.scal.ambi.entity.actions.ElementLikes

object LikesCommentsBinder {

    @JvmStatic
    @BindingAdapter(value = ["likesDescription", "likesDescriptionCurrentUser"], requireAll = true)
    fun bindLikesDescription(relativeLayout: RelativeLayout, likes: ElementLikes?, currentUser: User?) {
        relativeLayout.removeAllViews()
        if (null == likes || null == currentUser || likes.allUsersLiked.isEmpty()) {
            return
        }
        val allUsersExceptCurrent = likes.allUsersLiked.filter { it != currentUser }.toMutableList()
        if (likes.currentUserLiked) {
            allUsersExceptCurrent.add(0, currentUser)
        }
        val drawingUsers = allUsersExceptCurrent.subList(0, Math.min(3, allUsersExceptCurrent.size))

        val context = relativeLayout.context
        val inflater = LayoutInflater.from(context)
        var prevViewId = 0
        drawingUsers
            .forEach {
                val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.item_like_user_avatar_overlapped, relativeLayout, false)
                binding.setVariable(BR.user, it)

                val itemView = binding.root
                (itemView.layoutParams as RelativeLayout.LayoutParams).run {
                    if (0 != prevViewId) {
                        addRule(RelativeLayout.RIGHT_OF, prevViewId)
                    }
                    addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                    itemView.layoutParams = this
                }

                prevViewId = View.generateViewId()
                itemView.id = prevViewId
                relativeLayout.addView(itemView)
            }
        (0 until relativeLayout.childCount).map { relativeLayout.getChildAt(it) }.reversed().forEach { it.bringToFront() }

        val namesBinding = DataBindingUtil.inflate<ItemLikeUserNamesBinding>(inflater, R.layout.item_like_user_names, relativeLayout, false)
        namesBinding.text = generateNamesText(context, drawingUsers, currentUser)
        val namesItemView = namesBinding.root
        (namesItemView.layoutParams as RelativeLayout.LayoutParams).run {
            addRule(RelativeLayout.RIGHT_OF, prevViewId)
            addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
            namesItemView.layoutParams = this
        }
        relativeLayout.addView(namesItemView)
    }

    private fun generateNamesText(context: Context, drawingUsers: List<User>, currentUser: User): String {
        val namesText = StringBuilder(40)
        if (1 == drawingUsers.size) {
            return context.getString(R.string.text_likes_this, drawingUsers[0].name)
        } else {
            drawingUsers
                .map { if (it == currentUser) context.getString(R.string.text_you) else it.name }
                .mapIndexed { index, name ->
                    when (index) {
                        drawingUsers.size - 1 -> " ${context.getString(R.string.text_and)} $name"
                        0                     -> name
                        else                  -> ", $name"
                    }
                }
                .forEach { namesText.append(it) }
            namesText.append(" ")
            namesText.append(context.getString(R.string.text_like_this))
            return namesText.toString()
        }
    }
}