package io.scal.ambi.entity.exceptions

class GoodMessageException(val goodMessage: String, cause: Throwable? = null) : Exception(goodMessage, cause)