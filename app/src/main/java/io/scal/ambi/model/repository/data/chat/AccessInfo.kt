package io.scal.ambi.model.repository.data.chat

sealed class AccessInfo {

    object Nothing : AccessInfo()

    data class Data(val accessToken: String, val identity: String) : AccessInfo()
}