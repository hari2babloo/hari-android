package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Completable
import io.reactivex.Single
import io.scal.ambi.entity.chat.SmallChatItem
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.view.IconImageUser
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChatNewMessageInteractor @Inject constructor() : IChatNewMessageInteractor {

    override fun loadUserWithPrefix(): Single<List<User>> {
        // here we should load all data
        val random = SecureRandom()
        return Single.fromCallable {
            if (random.nextInt(10) > 3) {
                (0 until 15).map {
                    User.asStudent(UUID.randomUUID().toString(),
                                   IconImageUser("https://developers.google.com/web/images/contributors/philipwalton.jpg"),
                                   getRandomString(random.nextInt(8) + 1),
                                   getRandomString(random.nextInt(8) + 1)
                    )
                }
            } else {
                throw IllegalStateException("just test")
            }
        }
    }

    override fun createChat(selectedUsers: List<User>): Single<SmallChatItem> {
        return Completable.timer(5, TimeUnit.SECONDS).andThen(Single.error(IllegalStateException("not implemented yet")))
    }

    private val ALLOWED_CHARACTERS = "qwertyuiopasdfghjklzxcvbnm"

    private fun getRandomString(sizeOfRandomString: Int): String {
        val random = Random()
        val sb = StringBuilder(sizeOfRandomString)
        for (i in 0 until sizeOfRandomString)
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }
}