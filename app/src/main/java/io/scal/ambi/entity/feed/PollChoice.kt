package io.scal.ambi.entity.feed

import io.scal.ambi.entity.User

data class PollChoice(var uid: String, val text: String, val voters: List<User>) {

    companion object {

        fun createNew(text: String): PollChoice {
            return PollChoice("", text, emptyList())
        }
    }
}