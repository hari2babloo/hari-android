package io.scal.ambi.extensions.binding.binders

import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.ambi.work.BR
import com.ambi.work.R
import com.ambi.work.databinding.ItemLikeUserNamesBinding
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.generateNamesText
import io.scal.ambi.ui.home.newsfeed.list.data.UILikes

object LikesCommentsBinder {

    @JvmStatic
    @BindingAdapter(value = ["likesDescription", "likesDescriptionCurrentUser"], requireAll = true)
    fun bindLikesDescription(relativeLayout: RelativeLayout, likes: UILikes?, currentUser: User?) {
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
            .filter { it != currentUser }
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
        return generateNamesText(context, drawingUsers, R.string.text_likes_this, R.string.text_like_this, currentUser)
    }
}