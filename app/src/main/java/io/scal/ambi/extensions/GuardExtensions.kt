package io.scal.ambi.extensions

fun <T> T?.notNullOrThrow(fieldName: String): T {
    if (this == null) {
        throw IllegalStateException("$fieldName can not be null!")
    }
    return this
}

fun Boolean?.trueOrThrow(fieldName: String): Boolean {
    if (this != null && this) {
        return this
    } else {
        throw IllegalStateException("$fieldName should be true!")
    }
}