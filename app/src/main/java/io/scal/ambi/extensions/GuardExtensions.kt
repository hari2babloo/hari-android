package io.scal.ambi.extensions

fun <T> T?.notNullOrThrow(fieldName: String): T {
    if (this == null) {
        throw IllegalStateException("$fieldName can not be null!")
    }
    return this
}