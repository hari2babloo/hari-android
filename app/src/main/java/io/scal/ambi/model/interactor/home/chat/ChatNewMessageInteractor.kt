package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.view.IconImageUser
import java.security.SecureRandom
import java.util.*
import javax.inject.Inject

class ChatNewMessageInteractor @Inject constructor() : IChatNewMessageInteractor {

    override fun loadUserWithPrefix(searchString: String, page: Int): Single<List<User>> {
        val random = SecureRandom()
        return Single.fromCallable {
            if (page == 1 || random.nextInt(10) > 3) {
                (0 until 15).map {
                    val inName = random.nextBoolean()
                    User.asStudent(UUID.randomUUID().toString(),
                                   IconImageUser("https://developers.google.com/web/images/contributors/philipwalton.jpg"),
                                   (if (inName) "" else searchString) + getRandomString(random.nextInt(8)),
                                   (if (!inName) "" else searchString) + getRandomString(random.nextInt(8)))
                }
            } else {
                emptyList()
            }
        }
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