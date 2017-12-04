package io.scal.ambi.model.data.server.responses

interface Parceble<out T> {

    fun parse(): T
}