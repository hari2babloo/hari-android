package io.scal.ambi.entity.feed

data class PollChoice(var uid: String, val text: String, val voters: List<String>) {

    companion object {

        fun createNew(text: String): PollChoice {
            return PollChoice("", text, emptyList())
        }
    }
}